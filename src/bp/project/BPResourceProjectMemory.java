package bp.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.cache.BPCacheDataFileSystem;
import bp.cache.BPTreeCacheNode;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceVirtual;

public class BPResourceProjectMemory extends BPResourceVirtual implements BPResourceProject
{
	public BPResourceProjectMemory()
	{
		m_name = "Memory";
		m_children = new CopyOnWriteArrayList<BPResource>();
	}

	public void addChild(BPResource res)
	{
		m_children.add(res);
	}

	public void removeChild(BPResource res)
	{
		m_children.remove(res);
	}

	public void removeAll(List<BPResource> res)
	{
		m_children.removeAll(res);
	}

	public BPResource[] listResources()
	{
		return m_children.toArray(new BPResource[m_children.size()]);
	}

	public BPResourceDir getDir()
	{
		return null;
	}

	public String getResType()
	{
		return "memory project";
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public String getPath()
	{
		return null;
	}

	public boolean containResource(BPResource res)
	{
		return false;
	}

	public BPProjectItemFactory[] getItemFactories()
	{
		return null;
	}

	public String getProjectKey()
	{
		return "mem:" + toString();
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_name);
		return rc;
	}

	public void refreshByCache(BPTreeCacheNode<BPCacheDataFileSystem> root)
	{
	}

	public void save(BPResource res)
	{
	}

	public void savePrjFile()
	{
	}

	public boolean isTemp()
	{
		return true;
	}

	public String toString()
	{
		return "[M]" + m_name;
	}

	public BPResource wrapResource(BPResource res)
	{
		return res;
	}

	public String getProjectTypeName()
	{
		return "memory";
	}

	public static class BPProjectFactoryMemory implements BPProjectFactory
	{
		public BPResourceProject create(String prjtype, BPResourceDir dir, Map<String, String> prjdata)
		{
			BPResourceProject project = new BPResourceProjectMemory();
			if (prjdata.containsKey("name"))
				project.setName(prjdata.get("name"));
			return project;
		}

		public Class<? extends BPResourceProject> getProjectClass()
		{
			return BPResourceProjectMemory.class;
		}

		public List<String> getProjectTypes()
		{
			List<String> rc = new ArrayList<String>();
			rc.add("memory");
			return rc;
		}

		public boolean canHandle(String prjtype)
		{
			return prjtype.equalsIgnoreCase("memory");
		}

		public String getName()
		{
			return "Memory Project";
		}
	}
}
