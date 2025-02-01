package bp.res;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPResourceVirtual implements BPResource
{
	protected volatile String m_name;
	protected volatile BPResource m_parent;
	protected volatile List<BPResource> m_children;
	protected volatile boolean m_isleaf;
	protected volatile boolean m_readonly;
	protected volatile boolean m_canopen;
	protected volatile String m_id;
	protected volatile String m_customext;
	protected volatile boolean m_openwithtempid;

	public String getExt()
	{
		return m_customext;
	}

	public String getName()
	{
		return m_name;
	}

	public String getResType()
	{
		return "virtual";
	}

	public boolean isVirtual()
	{
		return true;
	}

	public BPResource getParentResource()
	{
		return m_parent;
	}

	public boolean isFileSystem()
	{
		return false;
	}

	public boolean isLeaf()
	{
		return m_isleaf;
	}

	public boolean canOpen()
	{
		return m_canopen;
	}

	public boolean isReadOnly()
	{
		return m_readonly;
	}

	public void setReadOnly(boolean flag)
	{
		m_readonly = flag;
	}

	public boolean needNetwork()
	{
		return false;
	}

	public boolean isWeb()
	{
		return false;
	}

	public boolean isLocal()
	{
		return true;
	}

	public boolean delete()
	{
		return false;
	}

	public boolean openWithTempID()
	{
		return m_openwithtempid;
	}

	public void release()
	{
		m_parent = null;
	}

	public String getID()
	{
		return m_id;
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public BPResource[] listResources(boolean isdelta)
	{
		return m_children != null ? m_children.toArray(new BPResource[m_children.size()]) : null;
	}

	public boolean isProjectResource()
	{
		return true;
	}

	public boolean rename(String newname)
	{
		return false;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_name);
		return rc;
	}

	public String getURI()
	{
		String rc = null;
		BPResource par = m_parent;
		if (par != null)
		{
			String paruri = par.getURI();
			if (paruri != null)
			{
				rc = paruri + "/" + m_name;
			}
		}
		return rc;
	}

	public void setChildren(List<BPResource> children)
	{
		m_children = new CopyOnWriteArrayList<BPResource>(children);
	}
}
