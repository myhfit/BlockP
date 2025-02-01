package bp.schedule;

import java.util.Date;

import bp.schedule.BPScheduleTarget.BPScheduleTargetParams;
import bp.util.Std;

public class BPScheduleTest extends BPScheduleTimerBase
{
	public BPScheduleTest()
	{
		m_time = 10000;
		m_target = BPScheduleTest::runTest;
	}

	protected final static void runTest(long ct, BPScheduleTargetParams params)
	{
		Std.info("Test Schedule@" + new Date(ct));
	}
}
