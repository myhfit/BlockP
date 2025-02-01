package bp.schedule;

import java.util.Map;

public interface BPScheduleFactory
{
	String getName();
	
	BPSchedule create(Map<String, Object> params);
	
	Class<? extends BPSchedule> getScheduleClass();
}