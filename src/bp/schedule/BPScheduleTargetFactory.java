package bp.schedule;

import java.util.Map;

public interface BPScheduleTargetFactory
{
	String getName();

	BPScheduleTarget create(Map<String, Object> params);
}
