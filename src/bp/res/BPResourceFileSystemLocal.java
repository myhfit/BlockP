package bp.res;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import bp.project.BPResourceProject;

public abstract class BPResourceFileSystemLocal implements BPResourceFileSystem
{
	protected volatile File m_file = null;
	protected volatile String m_tempid = null;

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

	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		if (this == other)
			return true;
		if (other instanceof BPResourceFileSystemLocal)
			return m_file.getAbsolutePath().equals(((BPResourceFileSystem) other).getFileFullName());
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
