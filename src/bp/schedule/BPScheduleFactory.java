package bp.schedule;

import bp.data.BPInstanceFactory;

public interface BPScheduleFactory extends BPInstanceFactory<BPSchedule>
{
	default Class<BPSchedule> getInstanceRootClass()
	{
		return BPSchedule.class;
	}

	public static String getFactoryTypeName()
	{
		return "Schedule";
	}
}