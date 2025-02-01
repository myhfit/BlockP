package bp.schedule;

import java.util.Map;

import bp.schedule.BPScheduleTarget.BPScheduleTargetParams;
import bp.util.ObjUtil;

public class BPScheduleFileSystem extends BPScheduleBase
{
	protected volatile String m_watchfile;

	public String getWatchFile()
	{
		return m_watchfile;
	}

	public void setWatchFile(String f)
	{
		m_watchfile = f;
	}

	public void check(BPScheduler scheduler, Object... datas)
	{
		if (!m_enabled)
			return;
		runInner(System.currentTimeMillis(), new BPScheduleTargetParams(scheduler, this, ObjUtil.makeMap("watchfile", datas[0], "context", datas[1], "kind", datas[2])));
	}

	public void prepare()
	{
	}

	public void run()
	{
		runInner(System.currentTimeMillis(), new BPScheduleTargetParams(null, this, null));
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		m_watchfile = (String) data.get("watchfile");
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		rc.put("watchfile", m_watchfile);
		return rc;
	}

	public boolean needMatchScheduler()
	{
		return true;
	}

	public boolean matchScheduler(BPScheduler scheduler)
	{
		return BPSchedulerFileSystem.NAME_FILESYSTEM.equals(scheduler.getName());
	}

	public final static class BPScheduleFactoryFileSystem implements BPScheduleFactory
	{
		public String getName()
		{
			return "FileSystem";
		}

		public BPSchedule create(Map<String, Object> params)
		{
			BPSchedule rc = new BPScheduleFileSystem();
			rc.setMappedData(params);
			return rc;
		}

		public Class<? extends BPSchedule> getScheduleClass()
		{
			return BPScheduleFileSystem.class;
		}
	}
}
