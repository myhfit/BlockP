package bp.env;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BPEnvDynamic extends BPEnvBase
{
	protected final List<String> m_rawkeys = new CopyOnWriteArrayList<String>();

	public boolean hasKey(String key)
	{
		return m_kvs.containsKey(key);
	}

	public boolean customKey()
	{
		return true;
	}

	public List<String> listRawKeys()
	{
		return new ArrayList<String>(m_rawkeys);
	}

	public boolean isRawKey(String key)
	{
		return m_rawkeys.contains(key);
	}

	public void addRawKey(String key)
	{
		if (!m_rawkeys.contains(key))
			m_rawkeys.add(key);
	}
	
	public static class BPEnvDynamicSimple extends BPEnvDynamic
	{
		private String m_name;

		public BPEnvDynamicSimple(String name)
		{
			m_name=name;
		}
		
		public String getName()
		{
			return m_name;
		}
		
	}
}
