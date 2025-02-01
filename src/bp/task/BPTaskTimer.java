package bp.task;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.ObjUtil;

public class BPTaskTimer extends BPTaskLocal<Boolean>
{
	protected volatile long m_interval = 1000;

	public final static ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);

	public BPTaskTimer()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "Timer";
	}

	protected void doStart()
	{
		Object[] ps = (Object[]) m_params;
		long ms = ObjUtil.toLong(ps[0], 0);
		if (ms <= 0)
		{
			setProgress(1f);
			triggerStatusChanged();
			m_future.complete(true);
		}
		else
		{
			long ct = System.currentTimeMillis();
			m_starttime = ct;
			long tar = ct + ms;
			boolean isbreak = false;
			setStarted();
			triggerStatusChanged();
			long c = tar - System.currentTimeMillis();
			while (c >= 0)
			{
				if (m_stopflag)
				{
					setCommonStatus(COMMON_STATUS_STOPPED);
					triggerStatusChanged();
					m_future.complete(false);
					isbreak = true;
					break;
				}
				setProgress(1f - ((float) c / (float) ms));
				setProgressText((ms - c) + "ms/" + ms + "ms");
				triggerStatusChanged();
				try
				{
					Thread.sleep(c > m_interval ? m_interval : c);
				}
				catch (InterruptedException e)
				{
				}
				c = tar - System.currentTimeMillis();
			}
			if (!isbreak)
			{
				setCompleted();
				m_future.complete(true);
			}
		}
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		rc.put("interval", m_interval);
		Object p0 = null;
		if (m_params != null)
		{
			Object[] arr = ((Object[]) m_params);
			if (arr.length > 0)
				p0 = arr[0];
		}
		rc.put("duration", ObjUtil.toLong(p0, 10000));
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		m_interval = ObjUtil.toLong(data.get("interval"), 1000);
		if (m_interval < 10)
			m_interval = 10;
		m_params = new Object[] { ObjUtil.toLong(data.get("duration"), 10000) };
	}

	public static class BPTaskFactoryTimer extends BPTaskFactoryBase<BPTaskTimer>
	{
		public String getName()
		{
			return "Timer";
		}

		protected BPTaskTimer createTask()
		{
			return new BPTaskTimer();
		}

		public Class<? extends BPTask<?>> getTaskClass()
		{
			return BPTaskTimer.class;
		}
	}
}
