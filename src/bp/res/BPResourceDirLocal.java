package bp.res;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.util.FileUtil;
import bp.util.Std;

public class BPResourceDirLocal extends BPResourceFileSystemLocal implements BPResourceDir
{
	public BPResourceDirLocal(String filename)
	{
		this(new File(filename.length() == 0 ? getAbsoluteFilename(filename) : fixOuterFilename(filename)));
	}

	public BPResourceDirLocal(File f)
	{
		m_file = f;
	}

	protected final static String fixOuterFilename(String filename)
	{
		return filename.startsWith("@") ? filename.substring(1) : filename;
	}

	protected final static String getAbsoluteFilename(String filename)
	{
		File f = new File(filename);
		return f.getAbsolutePath();
	}

	public String toString()
	{
		String name = m_file.getName();
		if (name != null && name.length() == 0)
			name = m_file.toString();
		return name;
	}

	public BPResourceFileSystem[] list()
	{
		File[] fs = m_file.listFiles();
		BPResourceFileSystem[] rc = null;
		if (fs != null)
		{
			rc = new BPResourceFileSystem[fs.length];
			for (int i = 0; i < fs.length; i++)
			{
				File f = fs[i];
				rc[i] = f.isDirectory() ? new BPResourceDirLocal(f) : new BPResourceFileLocal(f);
			}
		}
		return rc;
	}

	public void makeDir(String name)
	{
		File dir = new File(m_file, name);
		dir.mkdir();
	}

	public void makeDir()
	{
		m_file.mkdirs();
	}

	public BPResourceDir getDir(String name)
	{
		File dir = new File(m_file, name);
		if (!dir.exists())
		{
			dir.mkdir();
		}
		else if (dir.isFile())
		{
			return null;
		}
		return new BPResourceDirLocal(dir);
	}

	public BPResourceFileSystem createChild(String name, boolean isfile)
	{
		File f = new File(m_file, name);
		BPResourceFileSystem rc = null;
		try
		{
			if (isfile)
			{
				if (f.exists())
				{
					throw new RuntimeException(new FileAlreadyExistsException(f.getAbsolutePath()));
				}
				f.createNewFile();
				rc = new BPResourceFileLocal(f);
			}
			else
			{
				f.mkdir();
				rc = new BPResourceDirLocal(f);
			}
		}
		catch (IOException e)
		{
			Std.err(e);
		}
		return rc;
	}

	public BPResourceFileSystem getChild(String name)
	{
		return getChild(name, true);
	}

	public BPResourceFileSystem getChild(String name, boolean needexist)
	{
		File f = new File(m_file, name);
		BPResourceFileSystem rc = null;
		if (f.exists())
		{
			if (f.isDirectory())
				rc = new BPResourceDirLocal(f);
			else if (f.isFile())
				rc = new BPResourceFileLocal(f);
		}
		else if (!needexist)
		{
			rc = new BPResourceFileLocal(f);
		}
		return rc;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> map = super.getMappedData();
		return map;
	}

	public boolean delete(boolean isrecursive)
	{
		if (isrecursive)
		{
			return FileUtil.deleteDir(m_file);
		}
		else
			return super.delete(false);
	}

	public boolean copy(BPResource targetres)
	{
		List<String> names = listFileNames(true, null, null);
		if (targetres.isFileSystem())
		{
			BPResourceFileSystem target = (BPResourceFileSystem) targetres;
			String tarpath = target.getFileFullName();
			if (target.isDirectory())
			{
				boolean success = true;
				if (names != null)
				{
					for (String name : names)
					{
						File f = new File(m_file, name);
						File tar = new File(tarpath, name);
						if (f.isDirectory() && tar.exists() && tar.isDirectory())
						{
							// skip
						}
						else
						{
							try
							{
								Std.debug(f.getAbsolutePath() + ">" + tar.getAbsolutePath());
								Files.copy(f.toPath(), tar.toPath(), StandardCopyOption.REPLACE_EXISTING);
							}
							catch (IOException e)
							{
								success = false;
								Std.err(e);
								throw new RuntimeException(e);
							}
						}
					}
				}
				return success;
			}
			else
			{
				return false;
			}
		}
		return false;
	}

	public List<String> listFileNames(boolean isrecursive, List<String> list, String basepath)
	{
		List<String> rc = list == null ? new ArrayList<String>() : list;
		File[] fs = m_file.listFiles();
		String bp = basepath == null ? m_file.getAbsolutePath() : basepath;
		for (File f : fs)
		{
			if (f.isFile())
				rc.add(comparePath(bp, f.getAbsolutePath()));
			else if (f.isDirectory())
			{
				rc.add(comparePath(bp, f.getAbsolutePath()));
				if (isrecursive)
				{
					BPResourceDirLocal sub = new BPResourceDirLocal(f);
					sub.listFileNames(isrecursive, rc, bp);
				}
			}
		}
		return rc;
	}

	protected final static String comparePath(String basepath, String filename)
	{
		if (filename.startsWith(basepath))
		{
			return filename.substring(basepath.length());
		}
		else if ((filename + File.separator).equals(basepath))
		{
			return "";
		}
		else
		{
			return filename;
		}
	}
}
