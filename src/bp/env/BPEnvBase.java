package bp.env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BPEnvBase implements BPEnv
{
	protected Map<String, String> m_kvs = new ConcurrentHashMap<String, String>();

	public void setEnv(String key, String value)
	{
		if (!customKey() && !isRawKey(key))
			return;
		m_kvs.put(key, value);
	}

	public List<String> listKeys()
	{
		return new ArrayList<String>(m_kvs.keySet());
	}

	public String getValue(String key)
	{
		return m_kvs.get(key);
	}

	public abstract boolean hasKey(String key);

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>(m_kvs);
		List<String> rawkeys = listRawKeys();
		if (rawkeys != null && rawkeys.size() > 0)
		{
			for (String key : rawkeys)
			{
				if (!rc.containsKey(key))
					rc.put(key, null);
			}
		}
		return rc;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setMappedData(Map<String, Object> data)
	{
		if (customKey())
		{
			Map<String, Object> newkv = new HashMap<String, Object>();
			for (Entry<String, String> entry : m_kvs.entrySet())
			{
				String key = entry.getKey();
				if (isRawKey(key))
					newkv.put(key, entry.getValue());
			}
			for (Entry<String, Object> entry : data.entrySet())
			{
				String key = entry.getKey();
				if (key == null)
					continue;
				Object v = entry.getValue();
				if (v == null)
					v = "";
				newkv.put(key, v);
			}
			m_kvs.clear();
			m_kvs.putAll((Map) newkv);
		}
		else
		{
			for (Entry<String, Object> entry : data.entrySet())
			{
				String key = entry.getKey();
				if (isRawKey(key))
				{
					m_kvs.put(key, (String) entry.getValue());
				}
			}
		}
	}
}
