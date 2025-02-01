package bp.module;

import bp.util.DateUtil;

public abstract class BPModuleBase implements BPModule
{
	protected long m_loadtime;

	public void setLoadTime(long t)
	{
		m_loadtime = t;
	}

	public long getLoadTime()
	{
		return m_loadtime;
	}

	public String toString()
	{
		return "Module:" + getName() + ", Version:" + Integer.toString(getVersion()) + ", Load@" + DateUtil.formatTime(getLoadTime());
	}
}
