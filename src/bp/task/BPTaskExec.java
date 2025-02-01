package bp.task;

import java.io.IOException;
import java.util.Map;

import bp.BPCore;
import bp.res.BPResourceDirLocal;
import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.SystemUtil;
import bp.util.ThreadUtil;
import bp.util.ThreadUtil.ProcessThread;

public class BPTaskExec extends BPTaskLocal<Boolean>
{
	protected volatile String m_workdir;
	protected volatile String m_target;
	protected volatile String m_cmdparams;
	protected volatile boolean m_wait;
	protected volatile boolean m_syskill;
	protected volatile Integer m_exitcode;
	protected volatile boolean m_nostopflag;

	protected volatile Process m_process;

	public BPTaskExec()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "Execute";
	}

	public Integer getExitCode()
	{
		return m_exitcode;
	}

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
				String[] cmdarr = ThreadUtil.fixCommandArgs(m_target, m_cmdparams);
				Process p = new ProcessBuilder(cmdarr).directory(((BPResourceDirLocal) BPCore.getFileContext().getDir((m_workdir == null ? "." : m_workdir))).getFileObject()).redirectErrorStream(true).start();
				ProcessThread t = new ProcessThread(p);
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

	protected void tryStop()
	{
		if (m_syskill)
		{
			Process p = m_process;
			if (p != null)
				if (!SystemUtil.kill(p, true, true))
					p.destroyForcibly();
		}
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		rc.put("target", m_target);
		rc.put("params", m_cmdparams);
		rc.put("workdir", m_workdir);
		rc.put("wait", m_wait);
		if (m_syskill)
			rc.put("syskill", m_syskill);
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		m_target = (String) data.get("target");
		m_cmdparams = (String) data.get("params");
		m_workdir = (String) data.get("workdir");
		m_wait = ObjUtil.toBool(data.get("wait"), false);
		m_syskill = ObjUtil.toBool(data.get("syskill"), false);
	}

	public static class BPTaskFactoryExec extends BPTaskFactoryBase<BPTaskExec>
	{
		public String getName()
		{
			return "Execute";
		}

		protected BPTaskExec createTask()
		{
			return new BPTaskExec();
		}

		public Class<? extends BPTask<?>> getTaskClass()
		{
			return BPTaskExec.class;
		}
	}
}
