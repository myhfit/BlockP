package bp.schedule;

import bp.data.BPSLData;

public interface BPSchedule extends BPSLData, Runnable
{
	String getName();

	void check(BPScheduler scheduler, Object... datas);

	void prepare();

	boolean isEnabled();

	void setEnabled(boolean flag);

	default void relex()
	{

	}

	default boolean isTemp()
	{
		return false;
	}

	default boolean needMatchScheduler()
	{
		return false;
	}

	default boolean matchScheduler(BPScheduler scheduler)
	{
		return true;
	}
}
