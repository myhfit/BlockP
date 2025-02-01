package bp.schedule;

import java.util.List;

import bp.data.BPMData;

public interface BPScheduler extends BPMData
{
	String getName();

	void addSchedule(BPSchedule s);

	void removeSchedule(BPSchedule s);

	List<BPSchedule> getSchedules();

	void removeAll();

	void install();

	void uninstall();

	void runSchedule();
}
