package bp.project;

import java.util.Map;
import java.util.function.Consumer;

import bp.BPCore;
import bp.cache.BPEventCache;
import bp.event.BPEventCoreUI;
import bp.res.BPResourceDir;
import bp.util.ObjUtil;

public abstract class BPResourceProjectCached extends BPResourceProjectBase
{
	protected volatile String m_pathkey;
	protected volatile Consumer<BPEventCache> m_cachehandler;
	protected volatile boolean m_nocache;

	public BPResourceProjectCached(BPResourceDir dir, boolean nocache)
	{
		super(dir);
		m_cachehandler = this::onCacheChanged;
		m_nocache = nocache;
		m_pathkey = m_dir.getFileFullName();
	}

	public boolean canCache()
	{
		return true;
	}

	public void startCache()
	{
		if (m_dir.isFileSystem() && !m_nocache)
		{
			BPCore.EVENTS_CACHE.on(BPCore.getFileSystemCacheChannelID(), BPEventCache.EVENTKEY_CACHE_CHANGED, m_cachehandler);
			BPCore.FS_CACHE.addCacheTask(m_pathkey);
			BPCore.FS_CACHE.start();
		}
	}

	protected Map<String, String> saveToStringMap()
	{
		Map<String, String> rc = super.saveToStringMap();
		if (m_nocache)
			rc.put("nocache", "true");
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		m_nocache = ObjUtil.toBool(data.get("nocache"), false);
	}

	protected void onCacheChanged(BPEventCache event)
	{
		if (event.subkey.equals(m_pathkey))
		{
			BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.refreshProjectTree(m_pathkey));
		}
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> map = super.getMappedData();
		map.put("name", m_name);
		map.put("path", m_dir.getFileFullName());
		map.put("nocache", m_nocache ? "true" : "false");
		return map;
	}

	public void setNoCache(boolean nocache)
	{
		m_nocache = nocache;
	}

	public boolean isNoCache()
	{
		return m_nocache;
	}
}
