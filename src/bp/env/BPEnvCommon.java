package bp.env;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPEnvCommon extends BPEnvCustom
{
	public final static String ENV_NAME_COMMON = "Common";

	public final static String ENVKEY_ENABLE_SCHEDULE = "ENABLE_SCHEDULE";
	public final static String ENVKEY_AUTO_GC = "AUTO_GC";
	public final static String ENVKEY_ENABLE_SCHEDULER_FS = "ENABLE_SCHEDULER_FS";
	public final static String ENVKEY_ENABLE_MODULE_LOAD = "ENABLE_MODULE_LOAD";
	public final static String ENVKEY_RAWIO_BLOCKSIZE = "RAWIO_BLOCKSIZE";

	public String getName()
	{
		return ENV_NAME_COMMON;
	}

	protected List<String> setupRawKeys()
	{
		return new CopyOnWriteArrayList<String>(new String[] { ENVKEY_ENABLE_SCHEDULE, ENVKEY_AUTO_GC, ENVKEY_ENABLE_SCHEDULER_FS, ENVKEY_ENABLE_MODULE_LOAD, ENVKEY_RAWIO_BLOCKSIZE });
	}
}
