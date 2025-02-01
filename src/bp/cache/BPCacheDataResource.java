package bp.cache;

public interface BPCacheDataResource
{
	boolean isFile();

	boolean isDirectory();

	String getName();

	String getFullName();

	String getFullPath();
	
	boolean isFileSystem();
	
	boolean isLocal();
}
