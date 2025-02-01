package bp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import bp.util.IOUtil.ReadResourceResult;
import bp.util.IOUtil.ReadResourceResultHolder;

public class ZipUtil
{
	public final static boolean readTrees(String[] roots, InputStream in, BiFunction<ReadResourceResult, String, Boolean> callback)
	{
		boolean success = false;
		try (ZipInputStream zis = new ZipInputStream(in))
		{
			ZipEntry e = zis.getNextEntry();
			while (e != null)
			{
				String[] cr = checkRoot(roots, e);
				if (cr != null)
				{
					String root = cr[0];
					String path = cr[1];
					if (e.isDirectory())
					{
						ReadResourceResultHolder holder = new ReadResourceResultHolder();
						holder.datas = null;
						holder.isdir = true;
						holder.res = path;
						success = callback.apply(holder, root);
					}
					else
					{
						ReadResourceResultHolder holder = new ReadResourceResultHolder();
						holder.datas = IOUtil.read(zis);
						holder.isdir = false;
						holder.res = path;
						success = callback.apply(holder, root);
					}
				}
				e = zis.getNextEntry();
			}
		}
		catch (IOException e)
		{
			Std.err(e);
		}
		return success;
	}

	public final static byte[] readEntry(InputStream in, ZipEntry entry)
	{
		String entryname = entry.getName();
		byte[] bs = null;
		try (ZipInputStream zis = new ZipInputStream(in))
		{
			ZipEntry e = zis.getNextEntry();
			while (e != null)
			{
				if (entryname.equals(e.getName()))
				{
					bs = IOUtil.read(zis);
					break;
				}
				e = zis.getNextEntry();
			}
		}
		catch (IOException e1)
		{
			Std.err(e1);
		}
		return bs;
	}

	private final static String[] checkRoot(String[] roots, ZipEntry e)
	{
		String ename = e.getName();
		for (String root : roots)
		{
			if (ename.startsWith(root))
			{
				if (ename.length() == root.length() && root.endsWith("/"))
					return null;
				return new String[] { root, ename.substring(root.length()) };
			}
		}
		return null;
	}
}
