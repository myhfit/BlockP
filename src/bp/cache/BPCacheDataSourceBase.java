package bp.cache;

import bp.BPCore;
import bp.event.BPEventChannelBase;

public class BPCacheDataSourceBase extends BPCacheBase
{
	protected volatile String m_pathkey;
	protected volatile int m_channelid = BPCore.EVENTS_CACHE.addChannel(new BPEventChannelBase());

	public void clear()
	{
	}

	public int getEventChannelID()
	{
		return m_channelid;
	}

	public void setPathKey(String pathkey)
	{
		m_pathkey = pathkey;
	}

	protected boolean doCache()
	{
		return false;
	}
}
