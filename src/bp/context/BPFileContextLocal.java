package bp.context;

import java.util.List;

import bp.res.BPResourceFile;

public interface BPFileContextLocal extends BPFileContext, BPContextLocal
{
	String getBasePath();

	List<BPResourceFile> findRes(String filename, int limit);
}
