package bp.project;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.BPCore;
import bp.cache.BPCacheDataFileSystem;
import bp.cache.BPTreeCacheNode;
import bp.context.BPProjectsContext;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFileSystem;
import bp.util.FileUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public abstract class BPResourceProjectBase implements BPResourceProjectDir
{
	protected volatile BPResourceDir m_dir;
	protected volatile String m_name;
	protected volatile String m_path;
	protected volatile boolean m_disabled;

	public BPResourceProjectBase(BPResourceDir dir)
	{
		m_dir = dir;
	}

	public void setPath(String path)
	{
		m_path = path;
	}

	public String getPath()
	{
		return m_path;
	}

	public String getFileSystemName()
	{
		return m_dir.getFileSystemName();
	}

	public String getFileFullName()
	{
		return m_dir.getFileFullName();
	}

	public long getLastModified()
	{
		return m_dir.getLastModified();
	}

	public boolean rename(String newname)
	{
		return false;
	}

	public String getName()
	{
		return m_dir.getName();
	}

	public boolean isReadOnly()
	{
		return m_dir.isReadOnly();
	}

	public boolean needNetwork()
	{
		return m_dir.needNetwork();
	}

	public void release()
	{
		m_dir.release();
	}

	public BPResourceDir getDir()
	{
		return m_dir;
	}

	public BPResourceDir getDir(String name)
	{
		return m_dir.getDir(name);
	}

	public void makeDir(String name)
	{
		m_dir.makeDir(name);
	}

	public void makeDir()
	{
	}

	public String getURI()
	{
		return m_dir.getURI();
	}

	public BPResourceFileSystem[] list()
	{
		return m_dir.list();
	}

	public String toString()
	{
		return m_name;
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public void setEnabled(boolean flag)
	{
		m_disabled = !flag;
	}

	public BPResourceFileSystem getChild(String name)
	{
		return m_dir.getChild(name);
	}

	public BPResourceFileSystem getChild(String name, boolean needexist)
	{
		return m_dir.getChild(name, needexist);
	}

	public boolean exists()
	{
		return true;
	}

	public boolean delete()
	{
		deleteProjectFromContext(this);
		return false;
	}

	public BPResource getParentResource()
	{
		return m_dir.getParentResource();
	}

	public boolean copy(BPResource target)
	{
		return m_dir.copy(target);
	}

	public List<String> listFileNames(boolean isrecursive, List<String> list, String basepath)
	{
		return m_dir.listFileNames(isrecursive, list, basepath);
	}

	public boolean delete(boolean isrecursive)
	{
		deleteProjectFromContext(this);
		return false;
	}

	protected final static void deleteProjectFromContext(BPResourceProject project)
	{
		BPProjectsContext context = BPCore.getProjectsContext();
		context.removeProject(project);
		context.saveProjects();
		context.sendProjectChangedEvent();
	}

	public BPResourceFileSystem createChild(String name, boolean isfile)
	{
		return m_dir.createChild(name, isfile);
	}

	public boolean equals(Object other)
	{
		if (other != null)
		{
			if (this == other)
				return true;
			if (other instanceof BPResourceProject)
			{
				return m_dir.equals(((BPResourceProject) other).getDir());
			}
			else if (other instanceof BPResourceDir)
			{
				return m_dir.equals(other);
			}
		}
		return false;
	}

	public int hashCode()
	{
		return m_dir.hashCode();
	}

	public boolean isLocal()
	{
		return m_dir.isLocal();
	}

	protected Map<String, String> saveToStringMap()
	{
		Map<String, String> rc = new HashMap<String, String>();
		rc.put("PROJECTTYPE", getProjectTypeName());
		return rc;
	}

	protected void loadFromStringMap(Map<String, String> map)
	{

	}

	public void savePrjFile()
	{
		Map<String, String> prjmap = saveToStringMap();
		if (prjmap == null || prjmap.size() == 0)
		{
		}
		else
		{
			File f = new File(m_dir.getFileFullName(), S_FILENAME_PRJ);
			FileUtil.writeFile(f.getAbsolutePath(), TextUtil.fromString(TextUtil.fromPlainMap(prjmap, null), "utf-8"));
		}
	}

	public BPProjectItemFactory[] getItemFactories()
	{
		return new BPProjectItemFactory[0];
	}

	public String getProjectKey()
	{
		return m_dir.getFileFullName();
	}

	public void refreshByCache(BPTreeCacheNode<BPCacheDataFileSystem> root)
	{
	}

	public BPResource wrapResource(BPResource res)
	{
		return res;
	}

	public boolean isProjectResource()
	{
		return true;
	}

	public void setTempID(String tempid)
	{
		m_dir.setTempID(tempid);
	}

	public String getTempID()
	{
		return m_dir.getTempID();
	}

	public void save(BPResource res)
	{
	}

	public boolean containResource(BPResource res)
	{
		BPResource par = res.getParentResource();
		while (par != null)
		{
			if (m_dir.equals(par))
				return true;
			par = par.getParentResource();
		}
		return false;
	}

	public void addChild(BPResource res)
	{
	}

	public void removeChild(BPResource res)
	{
	}

	public void removeAll(List<BPResource> res)
	{
	}

	public Map<String, Object> getOverview()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", getName());
		rc.put("prjtype", getProjectTypeName());
		rc.put("extrakeys", ObjUtil.makeList("path"));
		rc.put("path", getPath());
		return rc;
	}

	public String getExt()
	{
		return "[PROJECT]";
	}
}