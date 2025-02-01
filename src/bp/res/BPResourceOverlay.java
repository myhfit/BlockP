package bp.res;

public abstract class BPResourceOverlay implements BPResource
{
	protected BPResource m_res;

	public final static String RESTYPE_OVERLAY = "overlay";

	public BPResourceOverlay(BPResource res)
	{
		m_res = res;
	}

	public String getExt()
	{
		return m_res.getExt();
	}

	public BPResource getRawResource()
	{
		return m_res;
	}

	public BPResource getParentResource()
	{
		return m_res.getParentResource();
	}

	public boolean isReadOnly()
	{
		return m_res.isReadOnly();
	}

	public boolean needNetwork()
	{
		return m_res.needNetwork();
	}

	public boolean isWeb()
	{
		return m_res.isWeb();
	}

	public boolean isLocal()
	{
		return m_res.isLocal();
	}

	public boolean isOverlay()
	{
		return true;
	}

	public boolean delete()
	{
		return m_res.delete();
	}

	public void release()
	{
		m_res.release();
		m_res = null;
	}

	public String getID()
	{
		return m_res.getID();
	}

	public String getResType()
	{
		return RESTYPE_OVERLAY;
	}

	public String getURI()
	{
		return m_res == null ? null : m_res.getURI();
	}

	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		if (this == other)
			return true;
		if (m_res == null)
			return false;
		if (other instanceof BPResourceOverlay)
			return m_res.equals(((BPResourceOverlay) other).getRawResource());
		return false;
	}
}
