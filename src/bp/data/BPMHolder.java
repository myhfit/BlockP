package bp.data;

public class BPMHolder<D extends BPMData> extends BPDataHolder implements BPMContainer<D>
{
	@SuppressWarnings("unchecked")
	public D readMData(boolean loadsub)
	{
		return (D) m_data;
	}

	public Boolean writeMData(D data, boolean savesub)
	{
		m_data = data;
		return true;
	}

}
