package bp.res;

public interface BPResourceFileSystem extends BPResource
{
	public final static String RESTYPE_FILE = "file";
	public final static String RESTYPE_DIR = "dir";

	default boolean isFileSystem()
	{
		return true;
	}

	boolean isFile();

	boolean isDirectory();

	default boolean isWeb()
	{
		return false;
	}

	boolean copy(BPResource target);

	boolean exists();

	boolean delete(boolean isrecursive);

	String getFileSystemName();

	String getFileFullName();

	default String getID()
	{
		String tmpid = getTempID();
		return tmpid != null ? tmpid : (getResType() + ":" + getFileSystemName() + ":" + getFileFullName());
	}

	void setTempID(String tempid);

	String getTempID();
	
	long getLastModified();
}
