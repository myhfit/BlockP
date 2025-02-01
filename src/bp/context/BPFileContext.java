package bp.context;

import bp.res.BPResource;
import bp.res.BPResourceDir;

public interface BPFileContext extends BPContext
{
	BPResource getRes(String filename);
	
	BPResourceDir getDir(String filename);

	BPResourceDir getRootDir();

	default boolean isProjectsContext()
	{
		return false;
	}
	
	String comparePath(String filename);
}
