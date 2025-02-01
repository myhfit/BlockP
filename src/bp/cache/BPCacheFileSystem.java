package bp.cache;

import java.util.List;

public interface BPCacheFileSystem extends BPCache
{
	public List<BPTreeCacheNode<BPCacheDataFileSystem>> searchFileByName(String filename, String ext, int limit);

	public void invalidate(String filename);

	public void refresh();
}
