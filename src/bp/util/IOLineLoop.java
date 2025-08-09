package bp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IOLineLoop
{
	protected volatile Thread m_thread;
	protected volatile boolean m_endflag;
	protected final ConcurrentLinkedDeque<IOLineHandler> m_cbs = new ConcurrentLinkedDeque<>();
	// protected volatile boolean m_cbchanged = false;

	protected volatile BufferedReader m_br;
	protected volatile InputStream m_in;
	protected volatile OutputStream m_out;

	protected final ReadWriteLock m_lock = new ReentrantReadWriteLock();
	protected final Object m_lo = new Object();

	public void startLoop()
	{
		m_endflag = false;
		m_thread = ThreadUtil.runNewThread(this::loop, false);
	}

	public void stopLoop()
	{
		m_endflag = true;
	}

	private void loop()
	{
		BufferedReader br = m_br;
		ConcurrentLinkedDeque<IOLineHandler> cbs = m_cbs;
		OutputStream out = m_out;
		IOLineHandler cb = cbs.peekLast();
		if (cb != null)
		{
			try
			{
				cb.writeStartHint(out, true);
			}
			catch (IOException e1)
			{
				Std.err(e1);
			}
		}
		while (true)
		{
			cb = cbs.peekLast();
			if (cb != null)
			{
				try
				{
					String line = br.readLine();
					if (line != null)
					{
						if (line.length() > 0)
						{
							if (cb.dealLine(line, this))
							{
								cbs.pollLast();
								// m_cbchanged = true;
							}
						}
						else
						{
							if (cb.dealEmptyLine(out, this))
							{
								cbs.pollLast();
								// m_cbchanged = true;
							}
						}

						if (cbs.size() == 0)
						{
							m_endflag = true;
						}
						else
						{
							cb = cbs.peekLast();
							cb.writeStartHint(out, line.length()>0);
						}
					}
				}
				catch (Exception e)
				{
					if (cb.dealException(e))
					{
						Std.err(e);
						cbs.pollLast();
						if (cbs.size() == 0)
							m_endflag = true;
					}
				}
				if (m_endflag)
				{
					synchronized (m_lo)
					{
						m_lo.notifyAll();
					}
					break;
				}
			}
			else
			{
				m_endflag = true;
			}
		}
	}

	public void setup(BufferedReader br, InputStream in, OutputStream out)
	{
		m_br = br;
		m_in = in;
		m_out = out;
	}

	public void addHandler(IOLineHandler handler)
	{
		m_cbs.add(handler);
		// m_cbchanged = true;
	}

	public void addHandlerAndWait(IOLineHandler handler)
	{
		addHandler(handler);

		BufferedReader br = m_br;
		ConcurrentLinkedDeque<IOLineHandler> cbs = m_cbs;
		OutputStream out = m_out;
		IOLineHandler cb = cbs.peekLast();
		while (true)
		{
			cb = cbs.peekLast();
			if (cb != null)
			{
				try
				{
					String line = br.readLine();
					if (line != null)
					{
						if (line.length() > 0)
						{
							if (cb.dealLine(line, this))
							{
								cbs.pollLast();
							}
						}
						else
						{
							if (cb.dealEmptyLine(out, this))
							{
								cbs.pollLast();
							}
						}
						cb = cbs.peekLast();
						if (cb != handler)
							return;
					}
				}
				catch (Exception e)
				{
					if (cb.dealException(e))
					{
						Std.err(e);
						cbs.pollLast();
						if (cbs.size() == 0)
							m_endflag = true;
					}
				}
				if (m_endflag)
				{
					synchronized (m_lo)
					{
						m_lo.notifyAll();
					}
					break;
				}
			}
		}
	}

	public OutputStream getOutputStream()
	{
		return m_out;
	}

	public void close()
	{
		try
		{
			m_br.close();
		}
		catch (IOException e)
		{
			Std.err(e);
		}
		m_br = null;
		m_in = null;
		m_out = null;
	}

	public static interface IOLineHandler
	{
		boolean dealLine(String line, IOLineLoop loop) throws IOException;

		default boolean dealEmptyLine(OutputStream out, IOLineLoop loop) throws IOException
		{
			return false;
		}

		void writeStartHint(OutputStream out, boolean newline) throws IOException;

		default boolean dealException(Exception e)
		{
			return true;
		}
	}

	public void waitLoop()
	{
		while (!m_endflag)
		{
			synchronized (m_lo)
			{
				try
				{
					m_lo.wait();
				}
				catch (InterruptedException e)
				{
				}
			}
		}
	}
}
