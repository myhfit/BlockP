package bp.res;

import java.util.List;

public interface BPResourceDir extends BPResourceFileSystem
{
	default String getExt()
	{
		return "[DIR]";
	}

	default boolean isFile()
	{
		return false;
	}

	default boolean isLeaf()
	{
		return false;
	}

	default boolean isDirectory()
	{
		return true;
	}

	default String getResType()
	{
		return RESTYPE_DIR;
	}
	
	default boolean isRoutable()
	{
		return true;
	}

	BPResourceDir getDir(String name);

	default List<String> listFileNames(boolean isrecursive)
	{
		return listFileNames(isrecursive, null, null);
	}

	List<String> listFileNames(boolean isrecursive, List<String> list, String basepath);

	BPResourceFileSystem getChild(String name);

	BPResourceFileSystem getChild(String name, boolean needexist);

	void makeDir(String name);

	void makeDir();

	BPResourceFileSystem[] list();

	default BPResource[] listResources(boolean isdelta)
	{
		return list();
	}

	BPResourceFileSystem createChild(String name, boolean isfile);
}
