package bp.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import bp.util.ClassUtil;

public class BPExtensionManager
{
	public final static BPExtensionLoader[] getExtensionLoaders()
	{
		BPExtensionLoader[] rc = null;
		ServiceLoader<BPExtensionLoader> infoloader = ClassUtil.getExtensionServices(BPExtensionLoader.class);
		List<BPExtensionLoader> infos = new ArrayList<BPExtensionLoader>();
		for (BPExtensionLoader info : infoloader)
		{
			if (!info.checkSystem())
				continue;
			infos.add(info);
		}
		rc = infos.toArray(new BPExtensionLoader[infos.size()]);
		return rc;
	}
}
