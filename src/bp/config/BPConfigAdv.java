package bp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface BPConfigAdv extends BPConfig
{
	<S extends BPConfigAdv> Consumer<S> getConfigLoader();

	<S extends BPConfigAdv> void setConfigLoader(Consumer<S> loader);

	<S extends BPConfigAdv> Consumer<S> getConfigPersister();

	<S extends BPConfigAdv> void setConfigPersister(Consumer<S> persister);

	default void load()
	{
		Consumer<BPConfigAdv> loader = getConfigLoader();
		if (loader != null)
			loader.accept(this);
	}

	default void save()
	{
		Consumer<BPConfigAdv> persister = getConfigPersister();
		if (persister != null)
			persister.accept(this);
	}

	void put(String k, Object v);

	void puts(Object... kvs);

	void putAll(Map<String, ?> map);

	public static abstract class BPConfigAdvBase implements BPConfigAdv
	{
		protected Map<String, Object> m_map;

		public BPConfigAdvBase()
		{
			m_map = createMap();
		}

		protected abstract Map<String, Object> createMap();

		@SuppressWarnings("unchecked")
		public <V> V get(String k)
		{
			return (V) m_map.get(k);
		}

		public void put(String k, Object v)
		{
			m_map.put(k, v);
		}

		public void puts(Object... kvs)
		{
			for (int i = 0; i < kvs.length; i += 2)
			{
				m_map.put((String) kvs[i], kvs[i + 1]);
			}
		}

		public void putAll(Map<String, ?> map)
		{
			m_map.putAll(map);
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
				if(data!=null)
					m_map.putAll(data);
			}
		}
	}
}
