package bp.env;

import java.lang.ref.WeakReference;
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

	public void clearEnv(String key)
	{
		if (!customKey() && !isRawKey(key))
			return;
		m_kvs.remove(key);
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

	public BPEnv getSub(String prefix)
	{
		return new BPEnvSub(this, getName() + ">" + prefix, prefix);
	}

	protected static class BPEnvSub implements BPEnv
	{
		protected WeakReference<BPEnv> m_parref;
		protected String m_prefix;
		protected String m_name;

		public BPEnvSub(BPEnv par, String name, String prefix)
		{
			m_parref = new WeakReference<BPEnv>(par);
			m_prefix = prefix;
			m_name = name;
		}

		public List<String> listKeys()
		{
			List<String> rc = new ArrayList<String>();
			String prefix = m_prefix;
			BPEnv par = m_parref.get();
			List<String> parkeys = par.listKeys();
			int l = prefix.length();
			for (String k : parkeys)
			{
				if (k.startsWith(prefix) && k.length() > l)
					rc.add(k.substring(l));
			}
			return rc;
		}

		public List<String> listRawKeys()
		{
			return listKeys();
		}

		public boolean isRawKey(String key)
		{
			return true;
		}

		public void setEnv(String key, String value)
		{
			m_parref.get().setEnv(m_prefix + key, value);
		}
		
		public void clearEnv(String key)
		{
			m_parref.get().clearEnv(m_prefix + key);
		}

		public String getName()
		{
			return m_name;
		}

		public String getValue(String key)
		{
			return m_parref.get().getValue(m_prefix + key);
		}

		public BPEnv getSub(String prefix)
		{
			return new BPEnvSub(this, m_name + ">" + prefix, prefix);
		}

		public Map<String, Object> getMappedData()
		{
			Map<String, Object> rc = new HashMap<String, Object>();
			BPEnv par = m_parref.get();
			String prefix = m_prefix;
			int l = prefix.length();
			for (String k : par.listKeys())
			{
				if (k.startsWith(prefix) && k.length() > l)
				{
					rc.put(k.substring(l), par.getValue(k));
				}
			}
			return rc;
		}

		public void setMappedData(Map<String, Object> data)
		{
			BPEnv par = m_parref.get();
			String prefix = m_prefix;
			int l = prefix.length();
			List<String> parkeys = par.listKeys();
			for (String k : parkeys)
			{
				if (k.startsWith(prefix) && k.length() > l)
				{
					par.clearEnv(k);
				}
			}
			for (String k : data.keySet())
			{
				par.setEnv(prefix + k, (String) data.get(k));
			}
		}
	}
}
