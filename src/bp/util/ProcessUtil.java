package bp.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ProcessUtil
{
	protected final static int DEFAULT_CACHE_SIZE = 4096;

	public static class ReadThread extends Thread
	{
		protected InputStream m_in;
		protected volatile boolean m_stopflag;
		protected BiConsumer<byte[], Integer> m_cb;
		protected Runnable m_endcb;

		public ReadThread(InputStream in, BiConsumer<byte[], Integer> cb, Runnable endcb)
		{
			setDaemon(true);
			m_in = in;
			m_cb = cb;
			m_endcb = endcb;
		}

		public ReadThread(InputStream in, BiConsumer<byte[], Integer> cb)
		{
			this(in, cb, null);
		}

		public void setStopFlag()
		{
			m_stopflag = true;
		}

		public void run()
		{
			byte[] bs = new byte[DEFAULT_CACHE_SIZE];
			InputStream in = m_in;
			BiConsumer<byte[], Integer> cb = m_cb;
			Runnable endcb = m_endcb;
			while (!m_stopflag)
			{
				try
				{
					int c = in.read(bs);
					if (c != -1)
					{
						if (c == 0)
						{
							try
							{
								Thread.sleep(100);
							}
							catch (InterruptedException e)
							{
							}
						}
						else
							cb.accept(bs, c);
					}
					else
						break;
				}
				catch (IOException e)
				{
					Std.err(e);
					break;
				}
			}
			if (endcb != null)
				endcb.run();
			m_endcb = null;
			m_in = null;
			m_cb = null;
		}
	}

	public static class DecodeStringThread extends Thread
	{
		protected PipedInputStream m_pis;
		protected PipedOutputStream m_pos;
		protected Consumer<String> m_cb;
		protected volatile boolean m_stopflag;
		protected String m_en;

		public DecodeStringThread(PipedOutputStream out, Consumer<String> cb, String en)
		{
			setDaemon(true);
			m_en = en;
			try
			{
				m_pis = new PipedInputStream(out, DEFAULT_CACHE_SIZE);
				m_pos = out;
			}
			catch (IOException e)
			{
			}
			m_cb = cb;
		}

		public void tryStop()
		{
			m_stopflag = true;
		}

		public void run()
		{
			PipedInputStream bis = m_pis;
			try (InputStreamReader isr = new InputStreamReader(bis, m_en); BufferedReader reader = new BufferedReader(isr))
			{
				boolean flag;
				boolean stopflag;
				char[] chs = new char[DEFAULT_CACHE_SIZE];
				Consumer<String> cb = m_cb;
				while (true)
				{
					flag = false;
					stopflag = m_stopflag;
					int c;
					c = reader.read(chs);
					while (c > -1)
					{
						flag = true;
						if (c > 0)
						{
							cb.accept(new String(chs, 0, c));
						}
						c = reader.read(chs);
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
				IOUtil.close(m_pis);
				IOUtil.close(m_pos);
				m_pis = null;
				m_pos = null;
			}
		}
	}

	public static class ReadWriteThread extends Thread
	{
		protected InputStream m_in;
		protected OutputStream m_out;
		protected volatile boolean m_stopflag;
		protected BiConsumer<byte[], Integer> m_cb;
		protected Runnable m_endcb;
		protected ConcurrentLinkedQueue<byte[]> m_cmds;

		public ReadWriteThread(InputStream in, OutputStream out, BiConsumer<byte[], Integer> cb, Runnable endcb)
		{
			setDaemon(true);
			m_in = in;
			m_out = out;
			m_cb = cb;
			m_endcb = endcb;
			m_cmds = new ConcurrentLinkedQueue<byte[]>();
		}

		public ReadWriteThread(InputStream in, OutputStream out, BiConsumer<byte[], Integer> cb)
		{
			this(in, out, cb, null);
		}

		public void setStopFlag()
		{
			m_stopflag = true;
		}

		protected boolean write2Process() throws IOException
		{
			boolean rc = false;
			ConcurrentLinkedQueue<byte[]> cmds = m_cmds;
			byte[] bs = cmds.poll();
			OutputStream out = m_out;
			while (bs != null)
			{
				out.write(bs);
				bs = cmds.poll();
				rc = true;
			}
			return rc;
		}

		public void write(byte[] bs)
		{
			m_cmds.offer(bs);
		}

		public void run()
		{
			byte[] bs = new byte[4096];
			InputStream in = m_in;
			BiConsumer<byte[], Integer> cb = m_cb;
			Runnable endcb = m_endcb;
			while (!m_stopflag)
			{
				try
				{
					if (in.available() > 0)
					{
						int c = in.read(bs);
						if (c != -1)
						{
							if (c != 0)
								cb.accept(bs, c);
						}
						else
						{
							break;
						}
					}
					else
					{
						if (write2Process())
						{
						}
						else
						{
							try
							{
								Thread.sleep(100);
							}
							catch (InterruptedException e)
							{
							}
						}
					}
				}
				catch (IOException e)
				{
					Std.err(e);
					break;
				}
			}
			if (endcb != null)
				endcb.run();
			m_endcb = null;
			m_in = null;
			m_cb = null;
		}
	}
}
