package bp.env;

import static bp.util.LockUtil.rwLock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bp.util.ClassUtil;
import bp.util.Std;

public class BPEnvManager
{
	protected final static Map<String, BPEnv> S_ENVMAP = new ConcurrentHashMap<String, BPEnv>();

	protected final static ReadWriteLock S_LOCK = new ReentrantReadWriteLock();

	public final static void init()
	{
		ServiceLoader<BPEnv> envs = ClassUtil.getExtensionServices(BPEnv.class);
		if (envs != null)
		{
			rwLock(S_LOCK, true, () ->
			{
				S_ENVMAP.clear();
				for (BPEnv env : envs)
				{
					String name = env.getName();
					S_ENVMAP.put(name, env);
				}
			});
		}
	}

	public final static String getEnvValue(String envname, String key)
	{
		BPEnv env = getEnv(envname);
		return env == null ? null : env.getValue(key);
	}

	public final static BPEnv getEnv(String envname)
	{
		return rwLock(S_LOCK, false, () ->
		{
			return S_ENVMAP.get(envname);
		});
	}

	public final static List<BPEnv> listEnv()
	{
		return rwLock(S_LOCK, false, () ->
		{
			return new ArrayList<BPEnv>(S_ENVMAP.values());
		});
	}

	public final static void setEnvs(Map<String, String> envs)
	{
		rwLock(S_LOCK, true, () ->
		{
			for (Entry<String, String> entry : envs.entrySet())
			{
				String keyname = entry.getKey();
				int vi = keyname.indexOf("|");
				if (vi < 0 || vi >= keyname.length() - 1)
					continue;
				String envname = keyname.substring(0, vi);
				String key = keyname.substring(vi + 1);
				BPEnv env = S_ENVMAP.get(envname);
				if (env != null)
				{
					env.setEnv(key, entry.getValue());
				}
				else
				{
					Std.err("Env:" + envname + " not exist");
				}
			}
		});
	}

	public final static void loadCustomEnvs()
	{
		List<BPEnv> envs = listEnv();
		for (BPEnv env : envs)
		{
			if (env.customSL())
				env.load();
		}
	}
}
