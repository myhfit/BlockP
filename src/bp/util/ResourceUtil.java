package bp.util;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import bp.BPCore;
import bp.cache.BPCacheDataResource;
import bp.res.BPResource;
import bp.res.BPResourceByteArray;
import bp.res.BPResourceDir;
import bp.res.BPResourceFileLocal;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceIO;

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

	public final static <T> T useTempResource(String prefix, String suffix, long len, Function<BPResourceIO, T> rescb)
	{
		T rc = null;
		if (len < 100485760)
		{
			BPResourceByteArray res = new BPResourceByteArray(new byte[(int) len], null, (suffix.startsWith(".") ? suffix : "." + suffix), BPCore.genID(BPCore.getFileContext()), "temp", true);
			try
			{
				rc = rescb.apply(res);
			}
			catch (Exception e)
			{
				Std.err(e);
			}
			finally
			{
				res.release();
			}
		}
		else
		{
			try
			{
				File newfile = File.createTempFile(prefix, suffix);
				BPResourceFileLocal res = new BPResourceFileLocal(newfile);
				rc = rescb.apply(res);
			}
			catch (IOException e)
			{
				Std.err(e);
			}
		}
		return rc;
	}

	public final static int copyResources(BPResource[] ress, BPResourceDir dir, BiConsumer<Integer, Integer> pcb)
	{
		int rc = 0;
		int reslen = ress.length;
		for (BPResource res : ress)
		{
			BPResourceFileSystem sres = dir.getChild(res.getName(), false);
			if (res.isLeaf())
			{
				if (res.isIO())
				{
					BPResourceIO fres = (BPResourceIO) res;
					Std.debug("Copy " + (fres.isFileSystem() ? ((BPResourceFileSystem) fres).getFileFullName() : fres.getName()) + ">" + sres.getFileFullName());
					long c = ((BPResourceIO) sres).useOutputStream(out -> ((BPResourceIO) res).useInputStream(in -> IOUtil.readAndWrite(in, out)));
					if (c < 0)
						Std.info_user("Error on copy");
				}
				else
				{
					Std.info_user("Can't read source");
				}
			}
			else
			{
				BPResourceFileSystem fres = (BPResourceFileSystem) res;
				if (res.isFileSystem() && res.isLocal())
				{
					File tar = new File(dir.getFileFullName(), fres.getName());
					if (!tar.exists())
						tar.mkdirs();
					FileUtil.copyDir(new File(fres.getFileFullName()), tar);
				}
			}
			rc++;
			pcb.accept(rc, reslen);
		}
		return rc;
	}
}
