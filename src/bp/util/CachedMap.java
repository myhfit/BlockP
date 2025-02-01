package bp.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CachedMap<K, V>
{
	protected int m_csize = 0;

	protected Map<K, V> m_map;
	protected Queue<K> m_keylist;

	public CachedMap()
	{
		m_map = new HashMap<K, V>();
		m_keylist = new LinkedList<K>();
	}

	public void setCacheSize(int size)
	{
		m_csize = size;
	}

	public V put(K k, V v)
	{
		V rc = m_map.put(k, v);
		if (m_keylist.contains(k))
			m_keylist.remove(k);
		m_keylist.add(k);
		int csize = m_csize;
		if (csize > 0 && m_map.size() > csize)
		{
			resize();
		}
		return rc;
	}

	public V get(K k)
	{
		return m_map.get(k);
	}

	public int size()
	{
		return m_map.size();
	}

	public void clear()
	{
		m_keylist.clear();
		m_map.clear();
	}

	public void putAll(Map<K, V> map)
	{
		m_map.putAll(map);
		Set<K> ks = map.keySet();
		for (K k : ks)
		{
			if (!m_keylist.contains(k))
				m_keylist.add(k);
		}
	}

	protected void resize()
	{
		int csize = m_csize;
		int s = m_map.size();
		if (csize > 0 && s > csize)
		{
			while (s > csize)
			{
				K k = m_keylist.poll();
				if (m_map.remove(k) != null)
					s--;
			}
		}
	}

	public void remove(K k)
	{
		m_map.remove(k);
		m_keylist.remove(k);
	}

	public boolean containsKey(K k)
	{
		return m_map.containsKey(k);
	}
}
