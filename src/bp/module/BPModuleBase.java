package bp.module;

import bp.util.DateUtil;

public abstract class BPModuleBase implements BPModule
{
	protected long m_loadtime;
	protected boolean m_initwithreplace;
	protected String m_name;

	public BPModuleBase()
	{
		m_name = getModuleName();
	}

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

	public void transferRootData(BPModule oldmodule)
	{
		m_initwithreplace = true;
		transferRootData(oldmodule.getRoot(), getRoot());
	}

	protected void transferRootData(Object oldroot, Object newroot)
	{

	}

	public void setNamePrefix(String prefix)
	{
		m_name = prefix + getModuleName();
	}

	public final String getName()
	{
		return m_name;
	}
}
