package bp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BPConfigCommon implements BPConfig
{
	protected Map<String, Object> m_map = new ConcurrentHashMap<String, Object>();

	@SuppressWarnings("unchecked")
	public <V> V get(String k)
	{
		return (V) m_map.get(k);
	}

	public Map<String, Object> getMappedData()
	{
		return new HashMap<String, Object>(m_map);
	}

	public void setMappedData(Map<String, Object> data)
	{
		if (m_map != null)
		{
			m_map.clear();
			if (data != null)
				m_map.putAll(data);
		}
	}
}
