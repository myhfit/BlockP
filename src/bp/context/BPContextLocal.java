package bp.context;

public interface BPContextLocal extends BPContext
{
	default boolean isLocal()
	{
		return true;
	}
}
