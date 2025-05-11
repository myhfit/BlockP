package bp.res;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.util.HashMap;
import java.util.Map;

import bp.project.BPResourceProject;
import bp.util.SystemUtil;
import bp.util.SystemUtil.SystemOS;

public abstract class BPResourceFileSystemLocal implements BPResourceFileSystem
{
	protected volatile File m_file = null;
	protected volatile String m_tempid = null;
	protected volatile String m_dname = null;

	public String getPathName()
	{
		return m_file.getAbsolutePath();
	}

	public boolean needNetwork()
	{
		return false;
	}

	public boolean isLocal()
	{
		return true;
	}

	public String getName()
	{
		return m_file.getName();
	}

	public boolean isReadOnly()
	{
		return m_file.canWrite();
	}

	public void release()
	{

	}

	public File getFileObject()
	{
		return m_file;
	}

	public boolean exists()
	{
		return m_file.exists();
	}

	public String getFileFullName()
	{
		return m_file.getAbsolutePath();
	}

	public boolean rename(String newname)
	{
		File fnew = new File(m_file.getParentFile(), newname);
		m_file.renameTo(fnew);
		m_file = fnew;
		return true;
	}

	public String getFileSystemName()
	{
		return "local";
	}

	public boolean delete()
	{
		return delete(true);
	}

	public boolean delete(boolean isrecursive)
	{
		return m_file.delete();
	}

	public BPResource getParentResource()
	{
		BPResource rc = null;
		File f = m_file.getParentFile();
		if (f != null && f.exists() && f.isDirectory())
		{
			rc = new BPResourceDirLocal(f);
		}
		return rc;
	}

	public int hashCode()
	{
		return m_file.hashCode();
	}

	public void setDisplayName(String dname)
	{
		m_dname = dname;
	}

	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		if (this == other)
			return true;
		if (other instanceof BPResourceFileSystemLocal)
		{
			if (m_file == null)
				return false;
			return m_file.equals(((BPResourceFileSystemLocal) other).m_file);
		}
		if (other instanceof BPResourceProject)
		{
			BPResourceDir odir = ((BPResourceProject) other).getDir();
			if (this == odir)
				return true;
			if (odir instanceof BPResourceFileSystemLocal)
				return m_file.getAbsolutePath().equals(odir.getFileFullName());
		}
		return false;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_file.getName());
		rc.put("lastmodified", getLastModified());

		if (SystemUtil.getOS() == SystemOS.Windows)
		{
			StringBuilder sb = new StringBuilder();
			try
			{
				DosFileAttributes attrs = Files.readAttributes(Paths.get(m_file.getAbsolutePath()), DosFileAttributes.class);
				sb.append(attrs.isArchive() ? "A" : " ");
				sb.append(attrs.isReadOnly() ? "R" : " ");
				sb.append(attrs.isSystem() ? "S" : " ");
				sb.append(attrs.isHidden() ? "H" : " ");
				sb.append(attrs.isSymbolicLink() ? "LINK" : " ");
				rc.put("creationtime", attrs.creationTime().toMillis());
				rc.put("accesstime", attrs.lastAccessTime().toMillis());
				rc.put("filekey", attrs.fileKey());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			rc.put("attrib", sb.toString());
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			try
			{
				BasicFileAttributes attrs = Files.readAttributes(Paths.get(m_file.getAbsolutePath()), BasicFileAttributes.class);
				rc.put("creationtime", attrs.creationTime().toMillis());
				rc.put("accesstime", attrs.lastAccessTime().toMillis());
				rc.put("filekey", attrs.fileKey());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			rc.put("attrib", sb.toString());
		}
		return rc;
	}

	public boolean isProjectResource()
	{
		return false;
	}

	public void setTempID(String tempid)
	{
		m_tempid = tempid;
	}

	public String getTempID()
	{
		return m_tempid;
	}

	public String getURI()
	{
		return m_file == null ? null : m_file.toURI().toString();
	}

	public long getLastModified()
	{
		return m_file.lastModified();
	}
}
