package bp.context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFile;
import bp.res.BPResourceDirLocal;
import bp.res.BPResourceFileLocal;
import bp.util.FileUtil;

public class BPFileContextLocalBase implements BPFileContextLocal
{
	protected String m_path;

	public final static String S_FILENAME_TASKS = ".bptasks";
	public final static String S_FILENAME_TASKJ = ".bptaskj";
	public final static String S_FILENAME_SCHEDULEJ = ".bpsdj";
	public final static String S_DIR_SCRIPTS = ".scripts";

	public BPFileContextLocalBase(String path)
	{
		File f = new File(path);
		m_path = f.getAbsolutePath();
		if (m_path.length() > 0 && !m_path.endsWith(File.separator) && !m_path.endsWith("/"))
		{
			m_path = m_path + File.separator;
		}
	}

	public BPResource getRes(String filename)
	{
		File f = FileUtil.getFile(m_path, filename);
		BPResource res = null;
		if (f.exists())
		{
			if (f.isFile())
				res = new BPResourceFileLocal(f);
			else if (f.isDirectory())
				res = new BPResourceDirLocal(f);
		}
		return res;
	}

	public BPResourceDir getDir(String filename)
	{
		BPResourceDirLocal file = null;
		if (filename.startsWith("@"))
		{
			file = new BPResourceDirLocal(filename.substring(1));
		}
		else
		{
			if (FileUtil.checkIsAbsolute(filename))
				file = new BPResourceDirLocal(filename);
			else
				file = new BPResourceDirLocal(m_path + filename);
		}
		return file;
	}

	public String getBasePath()
	{
		return m_path;
	}

	public List<BPResourceFile> findRes(String filename, int limit)
	{
		List<BPResourceFile> rc = new ArrayList<BPResourceFile>();
		return rc;
	}

	public BPResourceDir getRootDir()
	{
		return new BPResourceDirLocal(m_path);
	}

	public String comparePath(String filename)
	{
		if (filename.startsWith(m_path))
		{
			return filename.substring(m_path.length());
		}
		else if ((filename + File.separator).equals(m_path))
		{
			return "";
		}
		else
		{
			return filename;
		}
	}
}
