package bp.nativehelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BPNativeHelperBase implements BPNativeHelper
{
	protected final Map<String, Object> m_ifcs = new ConcurrentHashMap<String, Object>();

	@SuppressWarnings("unchecked")
	public <INTF> INTF getInterface(String name)
	{
		return (INTF) m_ifcs.get(name);
	}

	public void register(String name, Object intf)
	{
		m_ifcs.put(name, intf);
	}

	public void unregister(String name)
	{
		m_ifcs.remove(name);
	}
}