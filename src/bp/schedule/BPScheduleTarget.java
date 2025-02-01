package bp.schedule;

import java.util.Map;
import java.util.function.BiConsumer;

public interface BPScheduleTarget extends BiConsumer<Long, BPScheduleTarget.BPScheduleTargetParams>
{
	public static class BPScheduleTargetParams
	{
		public BPScheduler scheduler;
		public BPSchedule schedule;
		public Map<String, Object> datas;

		public BPScheduleTargetParams()
		{

		}

		public BPScheduleTargetParams(BPScheduler scheduler, BPSchedule schedule, Map<String, Object> datas)
		{
			this.scheduler = scheduler;
			this.schedule = schedule;
			this.datas = datas;
		}
	}
}