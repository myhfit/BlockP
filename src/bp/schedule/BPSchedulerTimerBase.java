package bp.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bp.util.LockUtil;
import bp.util.Std;
import bp.util.ThreadUtil;

public abstract class BPSchedulerTimerBase extends BPSchedulerBase implements BPSchedulerTimer
{
	protected volatile long m_interval = 1000;
	protected volatile ScheduledExecutorService m_es;
	protected final ReadWriteLock m_slock = new ReentrantReadWriteLock();

	public long getInterval()
	{
		return m_interval;
	}

	public void setInterval(long interval)
	{
		m_interval = interval;
	}

	public void install()
	{
		startPool();
		Std.debug("Scheduler:" + getName() + " Installed");
	}

	protected void startPool()
	{
		LockUtil.rwLock(m_slock, true, () ->
		{
			ScheduledExecutorService es = m_es;
			if (es == null)
			{
				m_es = Executors.newScheduledThreadPool(1, new ThreadUtil.DaemonThreadFactory());
				m_es.scheduleAtFixedRate(this::runSchedule, m_interval, m_interval, TimeUnit.MILLISECONDS);
			}
		});
	}

	protected void stopPool()
	{
		LockUtil.rwLock(m_slock, true, () ->
		{
			ScheduledExecutorService es = m_es;
			m_es = null;
			if (es != null)
			{
				es.shutdown();
			}
		});
	}

	public void uninstall()
	{
		stopPool();
		Std.debug("Scheduler:" + getName() + " Unistalled");
	}
}
