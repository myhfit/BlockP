package bp.schedule;

import bp.schedule.BPScheduleTarget.BPScheduleTargetParams;

public abstract class BPScheduleTimerBase extends BPScheduleBase
{
	protected volatile long m_time;
	protected volatile long m_counter;
	protected volatile long m_lasttime;

	public void check(BPScheduler scheduler, Object... datas)
	{
		long ct = System.currentTimeMillis();
		m_counter += ct - m_lasttime;
		boolean flag = false;
		if (m_counter >= m_time)
			flag = true;
		m_lasttime = ct;
		if (flag)
		{
			m_counter = 0;
			runInner(ct, new BPScheduleTargetParams(scheduler, this, null));
		}
	}

	public final void run()
	{
		m_counter = 0;
		long ct = System.currentTimeMillis();
		m_lasttime = ct;
		runInner(ct, null);
	}

	public void prepare()
	{
		m_counter = 0;
		m_lasttime = System.currentTimeMillis();
	}
}
