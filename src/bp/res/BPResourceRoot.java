package bp.res;

public interface BPResourceRoot extends BPResource
{
	default boolean isRoot()
	{
		return true;
	}
}
