package bp.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bp.util.ClassUtil;
import bp.util.LockUtil;
import bp.util.Std;

public class BPExtensionManager
{
	public final static Map<String, String> S_EXTCLSMAP = new ConcurrentHashMap<String, String>();
	public final static ReadWriteLock S_EXTSLOCK = new ReentrantReadWriteLock();

	public final static BPExtensionLoader[] getExtensionLoaders()
	{
		return getExtensionLoaders(false, null, null);
	}

	public final static BPExtensionLoader[] getExtensionLoaders(boolean errflag, List<String> filterclsnames,String blockprefix)
	{
		BPExtensionLoader[] rc = null;
		List<BPExtensionLoader> infos = new ArrayList<BPExtensionLoader>();
		List<String> clsnames = ClassUtil.readServiceNames(BPExtensionLoader.class, ClassUtil.getExtensionClassLoader(), blockprefix);
		for (String clsname : clsnames)
		{
			if (filterclsnames != null && (!filterclsnames.contains(clsname)))
				continue;
			try
			{
				BPExtensionLoader info = ClassUtil.createObject(clsname);
				if (info == null)
				{
					if (errflag)
						Std.debug("Error on load ext:" + clsname);
					continue;
				}
				if ((!info.checkPlatform()) || (!info.checkSystem()))
					continue;
				infos.add(info);
			}
			catch (Throwable e)
			{
				if (errflag)
					Std.debug("Error on load ext:" + clsname + "@" + e.getMessage());
			}
		}

		rc = infos.toArray(new BPExtensionLoader[infos.size()]);
		return rc;
	}

	public final static BPExtensionLoader[] getLoadedExtensionLoaders()
	{
		Map<String, String> clsmap = getLoadedExtensionLoaderClassMap();
		List<BPExtensionLoader> rc = new ArrayList<BPExtensionLoader>();
		Map<String, BPExtensionLoader> m = new HashMap<String, BPExtensionLoader>();
		BPExtensionLoader[] loaders = getExtensionLoaders(false, new ArrayList<String>(clsmap.values()), null);
		for (BPExtensionLoader loader : loaders)
			m.put(loader.getName(), loader);
		for (String name : clsmap.keySet())
		{
			BPExtensionLoader loader = m.get(name);
			if (loader != null)
				rc.add(loader);
		}
		return rc.toArray(new BPExtensionLoader[rc.size()]);
	}

	public final static List<String> getLoadedExtensionLoaderNames()
	{
		return LockUtil.rwLock(S_EXTSLOCK, false, () -> new ArrayList<String>(S_EXTCLSMAP.keySet()));
	}

	public final static Map<String, String> getLoadedExtensionLoaderClassMap()
	{
		return LockUtil.rwLock(S_EXTSLOCK, false, () -> new HashMap<String, String>(S_EXTCLSMAP));
	}

	public final static void setLoadedExtensionLoaders(List<BPExtensionLoader> loaders)
	{
		Map<String, String> clsmap = new HashMap<String, String>();
		for (BPExtensionLoader loader : loaders)
			clsmap.put(loader.getName(), loader.getClass().getName());
		LockUtil.rwLock(S_EXTSLOCK, true, () ->
		{
			S_EXTCLSMAP.clear();
			S_EXTCLSMAP.putAll(clsmap);
		});
	}

	public final static List<BPExtensionLoader> sortExtensionLoaders(BPExtensionLoader[] loaders)
	{
		List<BPExtensionLoader> ls = new ArrayList<BPExtensionLoader>();
		List<BPExtensionLoader> rc = new ArrayList<BPExtensionLoader>();
		List<String> names = new ArrayList<String>();
		for (BPExtensionLoader loader : loaders)
		{
			ls.add(loader);
		}
		List<BPExtensionLoader> ls2;
		while (ls.size() > 0)
		{
			ls2 = new ArrayList<BPExtensionLoader>();
			for (BPExtensionLoader loader : ls)
			{
				String[] exts = loader.getParentExts();
				boolean flag = exts == null;
				if (!flag)
				{
					boolean f2 = true;
					for (String ext : exts)
					{
						if (!names.contains(ext))
						{
							f2 = false;
							break;
						}
					}
					flag = f2;
				}
				if (flag)
				{
					rc.add(loader);
					names.add(loader.getName());
				}
				else
				{
					ls2.add(loader);
				}
			}
			if (ls.size() == ls2.size())
				break;
			ls.clear();
			ls = ls2;
		}
		return rc;
	}
}