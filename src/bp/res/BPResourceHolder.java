package bp.res;

public class BPResourceHolder extends BPResourceVirtual
{
	protected volatile Object m_data;
	protected volatile String m_ext;

	public BPResourceHolder(Object data, BPResource parent, String ext, String id, String name, boolean isleaf)
	{
		m_data = data;
		m_parent = parent;
		m_ext = ext;
		m_id = id;
		m_name = name;
		m_isleaf = isleaf;
	}

	@SuppressWarnings("unchecked")
	public <T> T getData()
	{
		return (T) m_data;
	}

	public Class<?> getDataClass()
	{
		return m_data.getClass();
	}

	public boolean isHold(Class<?> dataclass)
	{
		return dataclass.isInstance(m_data);
	}

	public String getExt()
	{
		return m_ext;
	}

	public String toString()
	{
		if (m_name == null)
			return super.toString();
		return (m_ext == null ? "" : (m_ext + ":")) + m_name;
	}

	public String getResType()
	{
		return "data holder";
	}

	public boolean canOpen()
	{
		return true;
	}

	public boolean delete()
	{
		BPResource parent = m_parent;
		if (parent != null && parent instanceof BPResourceParent)
		{
			((BPResourceParent) parent).removeChild(this);
			m_data = null;
			m_parent = null;
			super.delete();
			return true;
		}
		else
		{
			return false;
		}
	}

	public void release()
	{
		m_data = null;
		super.release();
	}

	public final static class BPResourceHolderW extends BPResourceHolder
	{
		public BPResourceHolderW(Object data, BPResource parent, String ext, String id, String name, boolean isleaf)
		{
			super(data, parent, ext, id, name, isleaf);
		}

		public void setData(Object data)
		{
			m_data = data;
		}
	}
}
