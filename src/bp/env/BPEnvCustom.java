package bp.env;

import java.util.ArrayList;
import java.util.List;

public abstract class BPEnvCustom extends BPEnvBase
{
	protected final List<String> m_rawkeys = setupRawKeys();

	protected abstract List<String> setupRawKeys();

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
}
