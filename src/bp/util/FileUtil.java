package bp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiPredicate;

import bp.BPCore;
import bp.res.BPResourceFileSystem;
import bp.util.IOUtil.ReadResourceResult;

public class FileUtil
{
	protected static volatile Boolean S_IGNORESENSITIVE = null;
	protected final static Object S_SENSITIVE_LOCK = new Object();

	private final static boolean checkIgnoreSensitive()
	{
		try
		{
			File a = new File(".bpprjs");
			File b = new File(".BPPRJS");
			return a.equals(b);
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		try
		{
			File a = new File(".bpenvs");
			File b = new File(".BPENVS");
			return a.equals(b);
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return false;
	}

	public final static boolean isIgnoreSensitive()
	{
		if (S_IGNORESENSITIVE == null)
		{
			boolean rc = false;
			synchronized (S_SENSITIVE_LOCK)
			{
				if (S_IGNORESENSITIVE == null)
				{
					rc = checkIgnoreSensitive();
					S_IGNORESENSITIVE = rc;
				}
				else
				{
					rc = S_IGNORESENSITIVE;
				}
			}
			return rc;
		}
		else
		{
			return S_IGNORESENSITIVE;
		}
	}

	public final static String getExt(String filename)
	{
		String ext = "";
		int vi = filename.lastIndexOf(".");
		if (vi > -1)
			ext = filename.substring(vi);
		return ext;
	}

	public final static byte[] readFile(String filename)
	{
		byte[] rc = null;
		File f = new File(filename);
		if (f.exists() && f.isFile() && f.canRead())
		{
			try (FileInputStream fis = new FileInputStream(filename))
			{
				rc = IOUtil.read(fis);
			}
			catch (IOException e)
			{
				Std.err(e);
			}
		}
		return rc;
	}

	public final static boolean checkIsAbsolute(String filename)
	{
		File[] roots = File.listRoots();
		for (File r : roots)
		{
			String rname = r.getAbsolutePath();
			if (rname.length() > 0 && filename.toLowerCase().startsWith(rname.toLowerCase()))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean writeFile(String filename, byte[] bs)
	{
		if (bs != null)
		{
			File f = new File(filename);
			if (f.canWrite() || !f.exists())
			{
				try (FileOutputStream fos = new FileOutputStream(filename))
				{
					return IOUtil.write(fos, bs);
				}
				catch (IOException e)
				{
					Std.err(e);
				}
			}
		}
		return false;
	}

	public final static boolean copyFile(File fsrc, File ftar)
	{
		String src = fsrc.getAbsolutePath();
		String tar = ftar.getAbsolutePath();
		try
		{
			Files.copy(Paths.get(src), Paths.get(tar));
			Std.debug("Copy " + src + ">" + tar);
			return true;
		}
		catch (IOException e)
		{
			Std.err(e);
		}
		return false;
	}

	public final static boolean copyDir(File src, File tarbase)
	{
		for (File s : src.listFiles())
		{
			if (s.isDirectory())
			{
				File tar = new File(tarbase, s.getName());
				tar.mkdir();
				copyDir(s, tar);
			}
			else if (s.isFile())
			{
				try
				{
					Files.copy(Paths.get(s.getAbsolutePath()), Paths.get(tarbase.getAbsolutePath(), s.getName()));
				}
				catch (IOException e)
				{
					Std.err(e);
					return false;
				}
			}
		}
		return true;
	}

	public final static boolean copyFileByReader(String tarbase, String path, ReadResourceResult reader)
	{
		String base = tarbase;
		boolean success = false;
		if (!base.endsWith("/") && (!base.endsWith(File.separator)))
		{
			base += File.separator;
		}
		String filename = base + path;
		FileOutputStream fos = null;
		try
		{
			File file = new File(filename);
			if (reader.isdir)
			{
				if (!file.exists())
					file.mkdirs();
			}
			else
			{
				File parfile = file.getParentFile();
				if (!parfile.exists())
					parfile.mkdirs();
				fos = new FileOutputStream(file);
				byte[] buffer = new byte[65536];
				int len = reader.read(buffer);
				while (len > -1)
				{
					fos.write(buffer, 0, len);
					len = reader.read(buffer);
				}
				fos.flush();
				success = true;
			}
		}
		catch (IOException e)
		{
			Std.err(e);
		}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException e)
				{
				}
			}
		}
		return success;
	}

	public static void forEachFile(File dir, boolean isrecusive, BiPredicate<File, File> filter)
	{
		File[] fs = dir.listFiles();
		if (fs != null)
		{
			for (File f : fs)
			{
				boolean flag = filter.test(dir, f);
				if (flag && f.isDirectory() && isrecusive)
				{
					forEachFile(f, true, filter);
				}
			}
		}
	}

	public final static boolean deleteDir(File dir)
	{
		File[] fs = dir.listFiles();
		if (fs != null)
		{
			for (File f : fs)
			{
				if (f.isDirectory())
				{
					if (!deleteDir(f))
						return false;
				}
				else
				{
					if (!f.delete())
						return false;
				}
			}
		}
		return dir.delete();
	}

	public final static boolean isFile(String filename)
	{
		File f = new File(filename);
		return f.exists() && f.isFile();
	}

	public final static boolean isDir(String filename)
	{
		File f = new File(filename);
		return f.exists() && f.isDirectory();
	}

	public final static File getFile(String path, String filename)
	{
		if (filename.equals(".") || filename.equals("./"))
			return new File(path);
		return new File(fixOuterFilename(path, filename));
	}

	public final static String getContextFileFullName(String filename)
	{
		BPResourceFileSystem fres = (BPResourceFileSystem) BPCore.getFileContext().getRes(filename);
		if (fres != null)
		{
			return fres.getFileFullName();
		}
		return null;
	}

	public final static String fixOuterFilename(String path, String filename)
	{
		return filename.startsWith("@") ? filename.substring(1) : ((path == null || path.length() == 0) ? filename : path + File.separator + filename);
	}

	public final static String[] splitPath(String path)
	{
		String p = path;
		char c = File.separatorChar;
		if (c != '/')
		{
			p = p.replace(c, '/');
		}
		return p.split("/");
	}
}
