package bp.cache;

import bp.event.BPEvent;

public class BPEventCache extends BPEvent
{
	public final static String EVENTKEY_CACHE_CHANGED = "E_CACHE_CHANGED";

	public String subkey;
	public Object[] datas;

	public BPEventCache(String subkey,Object... datas)
	{
		key = EVENTKEY_CACHE_CHANGED;
		this.subkey = subkey;
		this.datas=datas;
	}
}
