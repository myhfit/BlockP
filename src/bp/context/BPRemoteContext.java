package bp.context;

public interface BPRemoteContext extends BPContext
{
	default boolean isLocal()
	{
		return false;
	}
}
