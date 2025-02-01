package bp.res;

public interface BPResourceFile extends BPResourceFileSystem, BPResourceLeaf, BPResourceIO
{
	default String getExt()
	{
		return null;
	}

	default boolean isFile()
	{
		return true;
	}

	default boolean isDirectory()
	{
		return false;
	}

	default String getResType()
	{
		return RESTYPE_FILE;
	}

	default BPResource[] listResources(boolean isdelta)
	{
		return null;
	}

	default boolean canOpen()
	{
		return true;
	}

	long getSize();
}
