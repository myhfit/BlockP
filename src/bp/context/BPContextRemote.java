package bp.context;

import bp.remote.BPConnector;

public interface BPContextRemote extends BPContext
{
	default boolean isLocal()
	{
		return false;
	}

	void bindConnector(BPConnector conn);
}