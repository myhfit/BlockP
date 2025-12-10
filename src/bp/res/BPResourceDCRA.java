package bp.res;

import bp.data.BPDataContainerRandomAccess;

public class BPResourceDCRA extends BPResourceHolder
{
	public BPResourceDCRA(BPDataContainerRandomAccess data, BPResource parent, String ext, String id, String name, boolean isleaf)
	{
		super(data, parent, ext, id, name, isleaf);
	}
}
