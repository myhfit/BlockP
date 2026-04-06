package bp.res;

import bp.data.BPDataSource;
import bp.res.BPResourceVirtual.BPResourceVirtualEntity;

public abstract class BPResourceDataSource extends BPResourceVirtualEntity
{
	protected String m_filename;

	public abstract BPDataSource getDataSource();

	public void setDSLinkFilename(String filename)
	{
		m_filename = filename;
	}

	public String getDSLinkFilename()
	{
		return m_filename;
	}
}
