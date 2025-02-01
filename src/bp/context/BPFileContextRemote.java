package bp.context;

import java.util.List;

import bp.res.BPResourceFile;

public interface BPFileContextRemote extends BPFileContext, BPContextRemote
{
	String getBasePath();

	List<BPResourceFile> findRes(String filename, int limit);
}