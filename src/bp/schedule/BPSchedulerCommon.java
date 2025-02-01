package bp.schedule;

public class BPSchedulerCommon extends BPSchedulerTimerBase
{
	public final static String NAME_COMMON = "Common";

	public BPSchedulerCommon()
	{
		m_interval = 1000;
	}

	public String getName()
	{
		return NAME_COMMON;
	}
}
