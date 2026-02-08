package bp.task;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bp.BPCore;
import bp.machine.BPStateMachine;
import bp.machine.BPStateMachine.BPState;
import bp.machine.BPStateMachineBase;
import bp.machine.BPStateMachineSeq.BPStateBase;
import bp.res.BPResourceDirLocal;
import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.ProcessUtil;
import bp.util.Std;
import bp.util.ThreadUtil;
import bp.util.ThreadUtil.ProcessThread;

public class BPTaskExecStated extends BPTaskExec
{
	protected String m_stateparams;
	protected volatile PipedInputStream m_bis;
	protected volatile PipedOutputStream m_bos;
	protected AtomicBoolean m_outputstopflag = new AtomicBoolean();

	protected void doStart()
	{
		if (m_target != null && m_target.trim().length() > 0)
		{
			try
			{
				m_exitcode = null;
				if (m_syskill)
					m_nostopflag = true;
				setStarted();
				triggerStatusChanged();

				String[] cmdarr = ProcessUtil.fixCommandArgs(m_target, m_cmdparams);

				m_outputstopflag.set(false);
				PipedOutputStream bos = new PipedOutputStream();
				PipedInputStream bis = new PipedInputStream(bos);
				m_bos = bos;
				m_bis = bis;
				Map<String, Object> stps = JSONUtil.decode(m_stateparams);
				BPStateMachineExec m = new BPStateMachineExec();
				ByPassIO b = new ByPassIO();
				b.bis = m_bis;
				b.bos = m_bos;
				m.setup(stps, pg -> setProgress(pg), pgtext -> setProgressText(pgtext), (s, e) ->
				{
					if (s)
						setCompleted();
					else
						setFailed(e);
				}, () -> triggerStatusChanged(), m_outputstopflag);
				m.bind(b);
				ThreadUtil.runNewThread(m, true);

				Process p = new ProcessBuilder(cmdarr).directory(((BPResourceDirLocal) BPCore.getFileContext().getDir((m_workdir == null ? "." : m_workdir))).getFileObject()).redirectErrorStream(true).start();
				ProcessThread t = new ProcessThread(p);
				t.setOutputCollector((bs, len) ->
				{
					try
					{
						bos.write(bs, 0, len);
						bos.flush();
					}
					catch (IOException e)
					{
					}
				});
				t.start();
				if (m_wait)
				{
					m_process = p;
					ThreadUtil.doProcessLoop(p, t, () -> (!m_nostopflag) && m_stopflag, (stopflag, exitcode) ->
					{
						m_process = null;
						m_exitcode = p.exitValue();
						setCompleted();
						m_future.complete(true);
					});
				}
				else
				{
					setCompleted();
					m_future.complete(true);
				}
			}
			catch (IOException e)
			{
				Std.err(e);
				setFailed(e);
				m_future.completeExceptionally(e);
			}
		}
		else
		{
			RuntimeException re = new RuntimeException("target null");
			m_future.completeExceptionally(re);
			setFailed(re);
		}
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		rc.put("states", m_stateparams);
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		m_stateparams = (String) data.get("states");
	}

	public String getTaskName()
	{
		return "Execute with States";
	}

	public static class BPTaskFactoryExecStated extends BPTaskFactoryBase<BPTaskExecStated>
	{
		public String getName()
		{
			return "Execute with States";
		}

		protected BPTaskExecStated createTask()
		{
			return new BPTaskExecStated();
		}

		public Class<? extends BPTask<?>> getTaskClass()
		{
			return BPTaskExecStated.class;
		}
	}

	public static class BPStateMachineExecContext
	{
		protected Map<String, Object> m_stats;

		public BPStateMachineExecContext()
		{
			m_stats = new ConcurrentHashMap<String, Object>();
		}

		public void calcValue(String k, String op, double _v)
		{
			Object vn = m_stats.get(k);
			switch (op)
			{
				case "sum":
				{
					double v = 0d;
					if (vn != null)
						v = ((Number) vn).doubleValue();
					v += _v;
					m_stats.put(k, v);
					break;
				}
				case "average":
				{
					double v = 0d;
					long n = 0;
					if (vn != null)
					{
						v = ((Number) ((Object[]) vn)[1]).doubleValue();
						n = ((Number) ((Object[]) vn)[2]).longValue();
					}
					v += _v;
					n++;
					m_stats.put(k, new Object[] { (double) v / (double) n, v, n });
					break;
				}
			}
		}

		public void setValue(String k, String v)
		{
			m_stats.put(k, v);
		}

		public Object getValue(String k)
		{
			return m_stats.get(k);
		}
	}

	protected static class ByPassIO
	{
		public volatile PipedInputStream bis;
		public volatile PipedOutputStream bos;
	}

	public static class BPStateLogicExec implements BPMDataReflect
	{
		public String nextstate;
		public String matchrun;
		public String matchout;
		public List<String> keys;
		public List<String> ops;
		public List<String> finds;
		public boolean clearbuffer;

		@SuppressWarnings("unchecked")
		public void setMappedData(Map<String, Object> ps)
		{
			nextstate = (String) ps.get("nextstate");
			matchrun = (String) ps.get("matchrun");
			matchout = (String) ps.get("matchout");
			keys = (List<String>) ps.get("keys");
			ops = (List<String>) ps.get("ops");
			finds = (List<String>) ps.get("finds");
			clearbuffer = !ObjUtil.toBool(ps.get("bufferstr"), false);
		}

		public boolean matchRunLogic(String line)
		{
			if (matchrun != null)
				return line.matches(matchrun);
			return false;
		}

		public boolean matchOutLogic(String line)
		{
			if (matchout != null)
				return line.matches(matchout);
			return false;
		}

		public boolean runLogic(String line, BPStateMachineExecContext context)
		{
			if (finds != null && ops != null && keys != null)
			{
				int s = Math.min(ops.size(), finds.size());
				for (int i = 0; i < s; i++)
				{
					String op = ops.get(i);
					String find = finds.get(i);
					Matcher m = Pattern.compile(find).matcher(line);
					if (m.find())
					{
						String t = m.group();
						if (t != null && t.length() > 0)
						{
							if (op != null)
								context.calcValue(keys.get(i), op, ObjUtil.toDouble(t, 0d));
							else
								context.setValue(keys.get(i), t);
						}
					}
				}
			}
			return clearbuffer;
		}
	}

	public static class BPStateExecDynamic extends BPStateBase<String, BPStateMachineExecContext>
	{
		protected String m_name;
		protected List<BPStateLogicExec> m_logics;
		public boolean pgchanged;
		public boolean needclearbuffer;

		public void setup(String name, List<BPStateLogicExec> logics)
		{
			m_name = name;
			m_logics = new CopyOnWriteArrayList<BPStateLogicExec>(logics);
		}

		@SuppressWarnings("unchecked")
		public <T extends BPState<String, BPStateMachineExecContext>> T input(String e, BPStateMachineExecContext context, BPStateMachine<String, BPStateMachineExecContext, ?> machine)
		{
			pgchanged = false;
			needclearbuffer = false;
			for (BPStateLogicExec l : m_logics)
			{
				if (l.matchRunLogic(e))
				{
					pgchanged = true;
					needclearbuffer = l.runLogic(e, context);
				}
				if (l.matchOutLogic(e))
				{
					pgchanged = true;
					return (T) ((BPStateMachineExec) machine).getState(l.nextstate);
				}
			}
			return (T) this;
		}

		public void end(BPStateMachineExecContext context)
		{
		}
	}

	public static class BPStateMachineExec extends BPStateMachineBase<String, BPStateMachineExecContext, ByPassIO> implements Runnable
	{
		protected String m_pgkey;
		protected boolean m_pgtext;
		protected Runnable m_pgchanged;
		protected AtomicBoolean m_outputstopflag;
		protected Consumer<Float> m_setpgcb;
		protected Consumer<String> m_setpgtextcb;
		protected BiConsumer<Boolean, Throwable> m_endcb;
		protected volatile int m_statecount;
		protected int m_stateall;
		protected Map<String, Object> m_stateps;

		public BPStateMachineExec()
		{
			m_context = new BPStateMachineExecContext();
		}

		@SuppressWarnings("unchecked")
		public void setup(Map<String, Object> ps, Consumer<Float> pgcb, Consumer<String> pgtextcb, BiConsumer<Boolean, Throwable> endcb, Runnable pgchanged, AtomicBoolean outputstopflag)
		{
			m_stateps = (Map<String, Object>) ps.get("states");
			m_pgkey = (String) ps.get("pgkey");
			m_pgtext = ObjUtil.toBool(ps.get("pgtext"), false);
			m_pgchanged = pgchanged;
			m_setpgcb = pgcb;
			m_setpgtextcb = pgtextcb;
			m_endcb = endcb;
			m_outputstopflag = outputstopflag;
		}

		protected void setTaskProgressText(String text)
		{
			m_setpgtextcb.accept(text);
		}

		protected void setTaskEnd(boolean success, Throwable t)
		{
			m_endcb.accept(success, t);
		}

		protected void triggerChanged()
		{
			m_pgchanged.run();
		}

		protected void setTaskProgress()
		{
			float pg = 0f;
			if (m_pgkey != null)
			{
				String v = ObjUtil.toString(m_context.getValue(m_pgkey), null);
				if (v != null)
				{
					if (v.endsWith("%"))
						pg = ObjUtil.toFloat(v.substring(0, v.length() - 1).trim(), 0f)/100;
					else
						pg = ObjUtil.toFloat(v.trim(), 0f);
				}
			}
			else
			{
				pg = (float) m_statecount / (float) m_stateall;
			}
			m_setpgcb.accept(pg);
		}

		@SuppressWarnings("unchecked")
		public BPStateExecDynamic getState(String nextstate)
		{
			if (nextstate == null)
				return null;
			Map<String, Object> st = (Map<String, Object>) m_stateps.get(nextstate);
			BPStateExecDynamic rc = new BPStateExecDynamic();
			List<Map<String, Object>> logicps = (List<Map<String, Object>>) st.get("logics");
			List<BPStateLogicExec> logics = new ArrayList<BPStateLogicExec>();
			for (Map<String, Object> lp : logicps)
			{
				BPStateLogicExec l = new BPStateLogicExec();
				l.setMappedData(lp);
				logics.add(l);
			}
			rc.setup(nextstate, logics);
			return rc;
		}

		public void run()
		{
			PipedInputStream bis = m_src.bis;
			m_statecount = 0;
			m_cur = getState("start");
			boolean stateend = false;
			try (InputStreamReader isr = new InputStreamReader(bis); BufferedReader reader = new BufferedReader(isr))
			{
				boolean flag;
				boolean stopflag;
				LinkedList<String> lines = new LinkedList<String>();
				while (true)
				{
					flag = false;
					stopflag = m_outputstopflag.get();
					while (bis.available() > 0)
					{
						flag = true;
						String line = reader.readLine();
						if (stateend)
							continue;
						lines.add(line);
						int c = lines.size();
						while (c > 10000)
						{
							lines.remove();
							c--;
						}
						String ls = ObjUtil.joinDatas(lines, "\n", null, false);
						BPState<String, BPStateMachineExecContext> cur = m_cur;
						BPState<String, BPStateMachineExecContext> n = cur.input(ls, context(), this);
						if (n == null)
						{
							stateend = true;
							if (m_pgtext)
								setTaskProgressText(ObjUtil.toString(m_context));
							triggerChanged();
						}
						else if (cur != n)
						{
							m_statecount++;
							if (m_pgtext)
								setTaskProgressText(ObjUtil.toString(m_context));
							setTaskProgress();
							triggerChanged();
							lines.clear();
						}
						else if (((BPStateExecDynamic) cur).pgchanged)
						{
							if (m_pgtext)
								setTaskProgressText(ObjUtil.toString(m_context));
							setTaskProgress();
							triggerChanged();
							if (((BPStateExecDynamic) cur).needclearbuffer)
								lines.clear();
						}
						m_cur = n;
					}
					if (stopflag)
						break;
					if (!flag)
					{
						try
						{
							Thread.sleep(10);
						}
						catch (InterruptedException e)
						{
						}
					}
				}
			}
			catch (EOFException e)
			{
			}
			catch (IOException e)
			{
				Std.err(e);
			}
			finally
			{
				setTaskEnd(stateend, null);
				try
				{
					m_src.bis.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				try
				{
					m_src.bos.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
