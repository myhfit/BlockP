package bp.util;

import java.util.Map;
import java.util.function.Consumer;

public class MIF
{
	protected Map<String, ?> m_map;

	public MIF(Map<String, ?> map)
	{
		m_map = map;
	}

	public MIF mif(String key, Consumer<Object> cb)
	{
		if (m_map.containsKey(key))
		{
			cb.accept(m_map.get(key));
		}
		return this;
	}

	public MIF mifnull(String key, Consumer<Object> cbnotnull)
	{
		Object v = m_map.get(key);
		if (v != null)
		{
			cbnotnull.accept(v);
		}
		return this;
	}

	public MIF mifnull(String key, Consumer<Object> cbnotnull, Consumer<Object> cbnull)
	{
		Object v = m_map.get(key);
		if (v != null)
		{
			if (cbnotnull != null)
				cbnotnull.accept(v);
		}
		else
		{
			if (cbnull != null)
				cbnull.accept(null);
		}
		return this;
	}
}
