package bp.schedule;

public interface BPSchedulerTimer extends BPScheduler
{
	long getInterval();

	void setInterval(long interval);
}
