package bp.res;

public interface BPResourceFileSystemRemote extends BPResourceFileSystem
{
	default boolean needNetwork()
	{
		return true;
	}

	default boolean isLocal()
	{
		return false;
	}
}
