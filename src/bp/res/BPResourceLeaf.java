package bp.res;

public interface BPResourceLeaf extends BPResource
{
	default boolean isLeaf()
	{
		return true;
	}
}
