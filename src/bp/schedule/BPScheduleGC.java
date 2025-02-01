package bp.schedule;

import bp.schedule.BPScheduleTarget.BPScheduleTargetParams;

public class BPScheduleGC extends BPScheduleTimerBase
{
	public BPScheduleGC()
	{
		m_time = 10000;
		m_name = "Auto Garbage Collection";
		m_target = BPScheduleGC::runGC;
		m_blocktarget = true;
	}

	protected final static void runGC(long ct, BPScheduleTargetParams params)
	{
		try
		{
			System.gc();
		}
		catch (Error e)
		{
		}
	}

	public boolean isTemp()
	{
		return true;
	}
}
