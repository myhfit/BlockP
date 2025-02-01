package bp.util;

import bp.cache.BPCacheDataResource;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFileLocal;

public class ResourceUtil
{
	public final static BPResource getResourceFromFile(String filename)
	{
		return new BPResourceFileLocal(filename);
	}

	public final static BPResource getResourceFromCacheData(BPCacheDataResource cache)
	{
		BPResource rc = null;
		if (cache.isFileSystem() && cache.isFile() && cache.isLocal())
		{
			rc = new BPResourceFileLocal(cache.getFullName());
		}
		return rc;
	}

	public final static BPResource getResourceFromPath(BPResourceDir root, String[] path)
	{
		BPResourceDir d = root;
		BPResource rc = null;
		if (path.length == 0)
		{
			rc = d;
		}
		else
		{
			for (int i = 0; i < path.length; i++)
			{
				String name = path[i];
				BPResource res = d.getChild(name, i < path.length - 1);
				if (res == null)
					break;
				if ((i < path.length - 1))
					if (res.isLeaf())
						break;
					else
						d = (BPResourceDir) res;
				else
					rc = res;
			}
		}
		return rc;
	}
}
