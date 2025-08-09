package bp.ext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import bp.util.ClassUtil;
import bp.util.Std;

public class BPExtensionManager
{
	public final static BPExtensionLoader[] getExtensionLoaders()
	{
		return getExtensionLoaders(false);
	}

	public final static BPExtensionLoader[] getExtensionLoaders(boolean errflag)
	{
		BPExtensionLoader[] rc = null;
		ServiceLoader<BPExtensionLoader> infoloader = ClassUtil.getExtensionServices(BPExtensionLoader.class);
		List<BPExtensionLoader> infos = new ArrayList<BPExtensionLoader>();
		Iterator<BPExtensionLoader> it = infoloader.iterator();
		while (it.hasNext())
		{
			try
			{
				BPExtensionLoader info = it.next();

				if (!info.checkSystem())
					continue;
				infos.add(info);
			}
			catch (Throwable e)
			{
				if (errflag)
					Std.debug("Error on load ext:" + e.getMessage());
			}
		}
		rc = infos.toArray(new BPExtensionLoader[infos.size()]);
		return rc;
	}
}
