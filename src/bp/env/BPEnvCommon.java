package bp.env;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPEnvCommon extends BPEnvBase
{
	public final static String ENV_NAME_COMMON = "Common";

	public final static String ENVKEY_ENABLE_SCHEDULE = "ENABLE_SCHEDULE";
	public final static String ENVKEY_AUTO_GC = "AUTO_GC";
	public final static String ENVKEY_ENABLE_SCHEDULER_FS = "ENABLE_SCHEDULER_FS";
	public final static String ENVKEY_ENABLE_MODULE_LOAD = "ENABLE_MODULE_LOAD";
	public final static String ENVKEY_RAWIO_BLOCKSIZE = "RAWIO_BLOCKSIZE";

	private final List<String> m_rawkeys = new CopyOnWriteArrayList<String>(new String[] { ENVKEY_ENABLE_SCHEDULE, ENVKEY_AUTO_GC, ENVKEY_ENABLE_SCHEDULER_FS, ENVKEY_ENABLE_MODULE_LOAD, ENVKEY_RAWIO_BLOCKSIZE });

	public String getName()
	{
		return ENV_NAME_COMMON;
	}

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
