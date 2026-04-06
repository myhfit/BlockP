package bp.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bp.util.ClassUtil;
import bp.util.LockUtil;
import bp.util.Std;

public class BPExtensionManager
{
	public final static List<String> S_EXTS = new CopyOnWriteArrayList<String>();
	public final static ReadWriteLock S_EXTSLOCK = new ReentrantReadWriteLock();

	public final static BPExtensionLoader[] getExtensionLoaders()
	{
		return getExtensionLoaders(false);
	}

	public final static BPExtensionLoader[] getExtensionLoaders(boolean errflag)
	{
		BPExtensionLoader[] rc = null;
		List<BPExtensionLoader> infos = new ArrayList<BPExtensionLoader>();
		List<String> clsnames = ClassUtil.readServiceNames(BPExtensionLoader.class, ClassUtil.getExtensionClassLoader());
		for (String clsname : clsnames)
		{
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
		List<String> names = getLoadedExtensionLoaderNames();
		List<BPExtensionLoader> rc = new ArrayList<BPExtensionLoader>();
		Map<String, BPExtensionLoader> m = new HashMap<String, BPExtensionLoader>();
		BPExtensionLoader[] loaders = getExtensionLoaders();
		for (BPExtensionLoader loader : loaders)
			m.put(loader.getName(), loader);
		for (String name : names)
		{
			BPExtensionLoader loader = m.get(name);
			if (loader != null)
				rc.add(loader);
		}
		return rc.toArray(new BPExtensionLoader[rc.size()]);
	}

	public final static List<String> getLoadedExtensionLoaderNames()
	{
		List<String> rc = new ArrayList<String>();
		LockUtil.rwLock(S_EXTSLOCK, false, () -> rc.addAll(S_EXTS));
		return rc;
	}

	public final static void setLoadedExtensionLoaders(List<BPExtensionLoader> loaders)
	{
		List<String> names = new ArrayList<String>();
		for (BPExtensionLoader loader : loaders)
			names.add(loader.getName());
		LockUtil.rwLock(S_EXTSLOCK, true, () ->
		{
			S_EXTS.clear();
			S_EXTS.addAll(names);
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