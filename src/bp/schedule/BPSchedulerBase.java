package bp.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.util.Std;

public abstract class BPSchedulerBase implements BPScheduler
{
	protected List<BPSchedule> m_ss = new CopyOnWriteArrayList<BPSchedule>();

	public void addSchedule(BPSchedule s)
	{
		m_ss.add(s);
		s.prepare();
	}

	public void removeSchedule(BPSchedule s)
	{
		s.relex();
		m_ss.remove(s);
	}

	public List<BPSchedule> getSchedules()
	{
		return new ArrayList<BPSchedule>(m_ss);
	}

	public void removeAll()
	{
		m_ss.clear();
	}

	public final void runSchedule()
	{
		runScheduleInner();
	}

	protected void runScheduleInner()
	{
		List<BPSchedule> ss = new ArrayList<BPSchedule>(m_ss);
		for (BPSchedule s : ss)
		{
			try
			{
				s.check(this);
			}
			catch (Throwable e)
			{
				Std.err(e);
			}
		}
	}
}
