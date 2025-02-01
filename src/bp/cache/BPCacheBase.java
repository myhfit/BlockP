package bp.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import bp.util.ThreadUtil;

public abstract class BPCacheBase implements BPCache
{
	protected volatile int m_status = 0;

	protected volatile boolean m_stopflag = false;

	public final static int STATUS_STOPPED = 0;
	public final static int STATUS_RUNNING = 1;
	public final static int STATUS_COMPLETE = 2;

	protected ExecutorService m_pool = Executors.newSingleThreadExecutor(new ThreadUtil.DaemonThreadFactory());

	protected ReadWriteLock m_lock = new ReentrantReadWriteLock();

	public void start()
	{
		boolean canstart = withStatus((status) ->
		{
			boolean cs = (status == STATUS_STOPPED || status == STATUS_COMPLETE);
			status = STATUS_RUNNING;
			m_stopflag = false;
			return cs;
		});
		if (canstart)
		{
			startInner();
		}
	}

	protected void startInner()
	{
		m_pool.execute(() ->
		{
			boolean c = false;
			while (!c && !m_stopflag)
			{
				c = doCache();
			}
			end(c);
		});
	}

	protected void end(boolean c)
	{
		m_stopflag = false;
		withStatus((status) -> m_status = (c ? STATUS_COMPLETE : STATUS_STOPPED));
	}

	public void restart()
	{
		stop();
		clear();
		start();
	}

	public abstract void clear();

	public void stop()
	{
		m_stopflag = true;
	}

	protected boolean runCacheSegment(Runnable seg, boolean async)
	{
		boolean success = false;
		boolean canstart = withStatus((status) ->
		{
			boolean cs = (status == STATUS_STOPPED || status == STATUS_COMPLETE);
			status = STATUS_RUNNING;
			m_stopflag = false;
			return cs;
		});
		if (canstart)
		{
			if (async)
			{
				success = true;
				m_pool.execute(() ->
				{
					boolean c = false;
					try
					{
						seg.run();
						c = true;
					}
					finally
					{
						end(c);
					}
				});
			}
			else
			{
				try
				{
					seg.run();
					success = true;
				}
				finally
				{
					end(success);
				}
			}
		}
		return success;
	}

	protected <T> T withStatus(Function<Integer, T> cb)
	{
		Lock lock = m_lock.writeLock();
		lock.lock();
		try
		{
			T t = cb.apply(m_status);
			return t;
		}
		finally
		{
			lock.unlock();
		}
	}

	protected abstract boolean doCache();
}
