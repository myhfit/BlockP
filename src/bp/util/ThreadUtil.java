package bp.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadUtil
{
	private final static ExecutorService S_SHAREDTASKPOOL = Executors.newWorkStealingPool();

	private final static ExecutorService S_CACHEASYNCPOOL = Executors.newCachedThreadPool(new DaemonThreadFactory());

	public final static ThreadGroup exitCleanThreadGroup = new ThreadGroup("EXITCLEAN");

	public final static ExecutorService getSharedTaskPool()
	{
		return S_SHAREDTASKPOOL;
	}

	public final static ExecutorService getStandaloneTaskPool()
	{
		return Executors.newSingleThreadExecutor();
	}

	public final static ExecutorService getCacheAsyncPool()
	{
		return S_CACHEASYNCPOOL;
	}

	public final static void wrapContextClassLoader(Runnable seg, ClassLoader cl)
	{
		ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
		boolean needc = oldcl != cl;
		if (needc)
		{
			Thread.currentThread().setContextClassLoader(cl);
		}
		seg.run();
		if (needc)
		{
			Thread.currentThread().setContextClassLoader(oldcl);
		}
	}

	public final static Thread runNewThread(Runnable seg, boolean isdaemon)
	{
		Thread t = new Thread(seg);
		t.setDaemon(isdaemon);
		t.start();
		return t;
	}

	public final static class DaemonThreadFactory implements ThreadFactory
	{
		public Thread newThread(Runnable r)
		{
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		}

	}

	public static abstract class LaterConsumer<T> implements Consumer<T>
	{
		protected Object[] m_datas;

		public LaterConsumer(Object... datas)
		{
			m_datas = datas;
		}
	}

	public static abstract class LaterBiConsumer<T, E> implements BiConsumer<T, E>
	{
		protected Object[] m_datas;

		public LaterBiConsumer(Object... datas)
		{
			m_datas = datas;
		}
	}

	public final static void doProcessLoop(Process p, ProcessThread t, Supplier<Boolean> checkstopfunc, BiConsumer<Boolean, Integer> completefunc)
	{
		int killcount = 0;
		boolean stopflag = false;
		try
		{
			while (p.isAlive())
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
				}
				if (checkstopfunc.get())
				{
					stopflag = true;
					t.forceStop();
					if (killcount == 0)
					{
						p.destroyForcibly();
						killcount++;
					}
					else if (killcount < 300)
					{
						killcount++;
					}
					else if (killcount == 300)
					{
						p.destroyForcibly();
					}
				}
			}
			completefunc.accept(stopflag, p.exitValue());
		}
		finally
		{
		}
	}

	public static class ProcessThread extends Thread
	{
		protected Process m_process;
		protected volatile boolean m_stopflag = false;
		protected volatile WeakReference<BiConsumer<byte[], Integer>> m_outcollectorref = null;

		public ProcessThread(Process p)
		{
			m_process = p;
		}

		public void setOutputCollector(BiConsumer<byte[], Integer> collector)
		{
			m_outcollectorref = new WeakReference<BiConsumer<byte[], Integer>>(collector);
		}

		public void forceStop()
		{
			m_stopflag = true;
		}

		public void run()
		{
			Process process = m_process;
			if (process != null)
			{
				InputStream in = process.getInputStream();
				byte[] buffer = new byte[65536];
				BiConsumer<byte[], Integer> outcollector = null;
				WeakReference<BiConsumer<byte[], Integer>> outcollectorref = m_outcollectorref;
				if (outcollectorref != null)
					outcollector = outcollectorref.get();
				try
				{
					if (outcollectorref != null)
					{
						int c = in.read(buffer);
						while (c != -1)
						{
							if (m_stopflag)
								break;
							if (c > 0)
								outcollector.accept(buffer, c);
							c = in.read(buffer);
						}
					}
					else
					{
						int c = in.available();
						while (c > -1)
						{
							if (m_stopflag)
								break;
							if (c > 0)
							{
								if (in.read(buffer) == -1)
									break;
							}
							else
							{
								if (!process.isAlive())
									break;
								try
								{
									Thread.sleep(10);
								}
								catch (Exception e)
								{
								}
							}
							c = in.available();
						}
					}
				}
				catch (IOException e)
				{
					Std.err(e);
				}
				finally
				{
					try
					{
						in.close();
					}
					catch (IOException e)
					{
					}
				}
			}
		}
	}
}
