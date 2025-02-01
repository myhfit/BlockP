package bp.util;

import java.util.List;

import bp.BPCore;
import bp.schedule.BPSchedule;
import bp.schedule.BPScheduler;

public class ScheduleUtil
{
	public final static void addSchedule(BPSchedule sd)
	{
		if (sd.needMatchScheduler())
		{
			List<BPScheduler> ss = BPCore.getSchedulers();
			for (BPScheduler s : ss)
			{
				if (sd.matchScheduler(s))
				{
					s.addSchedule(sd);
					break;
				}
			}
		}
		else
		{
			BPCore.getCommonScheduler().addSchedule(sd);
		}
	}

	public final static void addScheduleAndSave(BPSchedule... sd)
	{
		for (BPSchedule s : sd)
		{
			addSchedule(s);
		}
		BPCore.saveSchedules();
	}

	public final static void addSchedulesAndSave(List<BPSchedule> sds)
	{

		BPCore.saveSchedules();
	}

	public final static void removeSchedulesAndSave(List<BPSchedule> sds)
	{
		for (BPSchedule sd : sds)
		{
			removeSchedule(sd);
		}
		BPCore.saveSchedules();
	}

	public final static void removeSchedule(BPSchedule sd)
	{
		if (sd.needMatchScheduler())
		{
			List<BPScheduler> ss = BPCore.getSchedulers();
			for (BPScheduler s : ss)
			{
				if (s.getSchedules().contains(sd))
				{
					s.removeSchedule(sd);
					break;
				}
			}
		}
		else
		{
			BPCore.getCommonScheduler().removeSchedule(sd);
		}
	}
}
