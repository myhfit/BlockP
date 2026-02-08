package bp.console;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

import bp.id.IDGenerator;
import bp.id.SerialIDGenerator;
import bp.util.IOUtil;
import bp.util.LockUtil;
import bp.util.LogicUtil.WeakRefGo;
import bp.util.ProcessUtil.DecodeStringThread;
import bp.util.ProcessUtil.ReadThread;
import bp.util.Std;
import bp.util.SystemUtil;

public class BPConsoleCLI extends BPConsoleBase<BPConsoleCLI.BPConsoleControllerCLI>
{
	protected Process m_process;

	protected String[] m_cmd;
	protected String m_workdir;

	protected PipedOutputStream m_pos;
	protected Lock m_lock;
	protected WeakRefGo<Runnable> m_cbref;
	protected StringBuffer m_sb;

	protected String m_en;
	protected DecodeStringThread m_strthread;

	protected static IDGenerator S_IDGEN = new SerialIDGenerator();

	public BPConsoleCLI()
	{
		m_lock = new ReentrantLock();
		m_en = SystemUtil.getSystemEncoding();
	}

	public void setEncoding(String en)
	{
		m_en = en;
	}

	public String getEncoding()
	{
		return m_en;
	}

	public void setCommand(String... cmd)
	{
		m_cmd = cmd;
		if (cmd[0] == null)
			cmd[0] = SystemUtil.getShellName();
		m_name = cmd[0] + "#" + S_IDGEN.genID();
	}

	public void setWorkdir(String workdir)
	{
		m_workdir = workdir;
	}

	public void setNotify(Runnable cb)
	{
		m_cbref = new WeakRefGo<Runnable>(cb);
	}

	protected Process createProcess() throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder(m_cmd);
		String workdir = m_workdir;
		if (workdir != null)
			pb.directory(new File(workdir));
		return pb.start();
	}

	protected void doStart() throws Exception
	{
		Process p = createProcess();
		if (p != null)
		{
			m_process = p;
			m_pos = new PipedOutputStream();
			m_sb = new StringBuffer();
			m_controller = createController(p.getInputStream(), p.getOutputStream(), p.getErrorStream());
			m_controller.startThread();
			m_strthread = new DecodeStringThread(m_pos, this::onString, m_en);
			m_strthread.start();
		}
	}

	public String dlString()
	{
		return LockUtil.lock(m_lock, () ->
		{
			String rc = m_sb.toString();
			m_sb.setLength(0);
			return rc;
		});
	}

	protected BPConsoleControllerCLI createController(InputStream in, OutputStream out, InputStream err)
	{
		BPConsoleControllerCLI rc = new BPConsoleControllerCLI(in, out, err, m_en, this::onControllerEnd);
		rc.setup(this::onInput, this::onError);
		return rc;
	}

	protected void onControllerEnd()
	{
		DecodeStringThread strthread = m_strthread;
		if (strthread != null)
			strthread.tryStop();
		m_strthread = null;
		IOUtil.close(m_pos);
		m_pos = null;
	}

	protected void onInput(byte[] bs, int c)
	{
		OutputStream out = m_pos;
		try
		{
			out.write(bs, 0, c);
			out.flush();
		}
		catch (IOException e)
		{
			Std.err(e);
		}
	}

	protected void onError(byte[] bs, int c)
	{
		OutputStream out = m_pos;
		try
		{
			out.write(bs, 0, c);
			out.flush();
		}
		catch (IOException e)
		{
			Std.err(e);
		}
	}

	protected void onString(String str)
	{
		LockUtil.lock(m_lock, () ->
		{
			m_sb.append(str);
		});
		m_cbref.callRunnable();
	}

	protected void doStop() throws Exception
	{
		Process p = m_process;
		BPConsoleControllerCLI c = m_controller;
		m_process = null;
		m_controller = null;
		if (p != null)
			p.destroy();
		if (c != null)
			c.stopThread();
	}

	public static class BPConsoleControllerCLI implements BPConsoleController
	{
		protected ReadThread m_inthread;
		protected ReadThread m_errthread;

		protected InputStream m_in;
		protected OutputStream m_out;
		protected InputStream m_err;
		protected String m_en;
		protected Runnable m_endcb;

		protected Lock m_lock;

		public BPConsoleControllerCLI(InputStream in, OutputStream out, InputStream err, String en, Runnable endcb)
		{
			m_in = in;
			m_out = out;
			m_err = err;
			m_en = en;
			m_lock = new ReentrantLock();
			m_endcb = endcb;
		}

		public boolean writeString(char[] chs)
		{
			OutputStream out = m_out;
			if (out != null)
			{
				try
				{
					out.write((new String(chs) + "\n").getBytes(m_en));
					out.flush();
					return true;
				}
				catch (IOException e)
				{
					Std.err(e);
				}
			}
			return false;
		}

		public boolean writeChar(char ch)
		{
			try
			{
				m_out.write((ch + "").getBytes(m_en));
				return true;
			}
			catch (IOException e)
			{
				Std.err(e);
			}
			return false;
		}

		public void setup(BiConsumer<byte[], Integer> incb, BiConsumer<byte[], Integer> errcb)
		{
			m_inthread = new ReadThread(m_in, incb, this::onEndReadThread);
			m_errthread = new ReadThread(m_err, errcb);
		}

		protected void onEndReadThread()
		{
			m_lock.lock();
			try
			{
				m_out = null;
				m_in = null;
				m_err = null;
				m_inthread = null;
				m_errthread = null;
			}
			finally
			{
				m_lock.unlock();
			}
			try
			{
				Runnable endcb = m_endcb;
				m_endcb = null;
				if (endcb != null)
					endcb.run();
			}
			catch (Exception e)
			{

			}
		}

		public void startThread()
		{
			m_lock.lock();
			ReadThread inthread;
			ReadThread errthread;
			try
			{
				inthread = m_inthread;
				errthread = m_errthread;
				if (inthread != null)
					inthread.start();
				if (errthread != null)
					errthread.start();
			}
			finally
			{
				m_lock.unlock();
			}
		}

		public void stopThread()
		{
			m_lock.lock();
			try
			{
				ReadThread inthread = m_inthread;
				ReadThread errthread = m_errthread;
				if (inthread != null)
					inthread.setStopFlag();
				if (errthread != null)
					errthread.setStopFlag();
			}
			finally
			{
				m_lock.unlock();
			}
		}
	}
}