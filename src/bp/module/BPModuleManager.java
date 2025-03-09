package bp.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bp.env.BPEnvCommon;
import bp.env.BPEnvManager;
import bp.util.ClassUtil;
import bp.util.ClassUtil.BPExtClassLoader;
import bp.util.LockUtil;
import bp.util.Std;

public class BPModuleManager
{
	private final static ConcurrentHashMap<String, BPModule> S_MODULES = new ConcurrentHashMap<String, BPModule>();

	private final static ReadWriteLock S_MLOCK = new ReentrantReadWriteLock();

	public final static List<String> getModuleNames()
	{
		List<BPModule> modules = getModules();
		List<String> rc = new ArrayList<String>();
		for (BPModule m : modules)
			rc.add(m.getName());
		return rc;
	}

	public final static List<BPModule> getModules()
	{
		return LockUtil.rwLock(S_MLOCK, false, () -> new ArrayList<BPModule>(S_MODULES.values()));
	}

	public final static BPModule getModule(String name)
	{
		return LockUtil.rwLock(S_MLOCK, false, () -> S_MODULES.get(name));
	}

	@SuppressWarnings("unchecked")
	public final static <M extends BPModule> M loadModule(String filename)
	{
		if (!("true".equalsIgnoreCase(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_ENABLE_MODULE_LOAD))))
			return null;
		Object rc = null;
		BPExtClassLoader extcl = new BPExtClassLoader(ClassUtil.getExtensionClassLoader());
		extcl.addExtURL(filename);
		boolean loadflag = false;
		try
		{
			ServiceLoader<BPModule> sl = ServiceLoader.load(BPModule.class, extcl);
			BPModule m = null;
			{
				Iterator<BPModule> it = sl.iterator();
				if (it.hasNext())
					m = it.next();
			}
			if (m != null && m.test())
			{
				m.setLoadTime(System.currentTimeMillis());
				Object root = m.createRootInstance();
				if (m.initRoot(root))
				{
					putModule(m);
					m.initRootData();
					rc = m;
					loadflag = true;
				}
			}
		}
		finally
		{
			if (!loadflag)
			{
				tryCloseExtClassLoader(extcl);
				Std.err("Load Module Failed:" + filename);
			}
			else
			{
				Std.info("Load Module Success:" + filename);
			}
		}

		return (M) rc;
	}

	@SuppressWarnings("unchecked")
	public final static <M extends BPModule> List<M> loadModules(String filename)
	{
		if (!("true".equalsIgnoreCase(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_ENABLE_MODULE_LOAD))))
			return null;
		List<M> rc = new ArrayList<M>();
		List<M> roots = new ArrayList<M>();
		BPExtClassLoader extcl = new BPExtClassLoader(ClassUtil.getExtensionClassLoader());
		extcl.addExtURL(filename);
		boolean loadflag = false;
		try
		{
			ServiceLoader<BPModule> sl = ServiceLoader.load(BPModule.class, extcl);
			for (BPModule m : sl)
			{
				if (m.test())
				{
					m.setLoadTime(System.currentTimeMillis());
					Object root = m.createRootInstance();
					if (m.initRoot(root))
					{
						putModule(m);
						m.initRootData();
						roots.add((M) m);
					}
				}
			}
			loadflag = true;
		}
		finally
		{
			if (!loadflag)
			{
				tryCloseExtClassLoader(extcl);
				Std.err("Load Module Failed:" + filename);
			}
			else
			{
				rc.addAll(roots);
				Std.debug("Load Module Success:" + filename);
			}
		}

		return rc;
	}

	private final static void putModule(BPModule m)
	{
		BPModule oldm = LockUtil.rwLock(S_MLOCK, true, () ->
		{
			String mname = m.getName();
			BPModule om = S_MODULES.get(mname);
			S_MODULES.put(mname, m);
			return om;
		});
		if (oldm != null)
		{
			oldm.transferRootData(m);
			oldm.unload();
			tryCloseExtClassLoader((BPExtClassLoader) oldm.getClass().getClassLoader());
		}
	}

	protected final static void tryCloseExtClassLoader(BPExtClassLoader cl)
	{
		try
		{
			cl.close();
		}
		catch (IOException e)
		{
			Std.err(e);
		}
	}

	public final static boolean unloadModule(String mname)
	{
		BPModule m = LockUtil.rwLock(S_MLOCK, true, () ->
		{
			return S_MODULES.remove(mname);
		});
		if (m != null)
		{
			m.unload();
			tryCloseExtClassLoader((BPExtClassLoader) m.getClass().getClassLoader());
			Std.debug("Unload Module Success:" + mname);
			return true;
		}
		return false;
	}
}
