package bp.cache;

import java.util.List;
import java.util.function.Predicate;

public interface BPCacheFileSystem extends BPCache
{
	public List<BPTreeCacheNode<BPCacheDataFileSystem>> searchFileByName(String filename, String ext, int limit);

	public List<BPTreeCacheNode<BPCacheDataFileSystem>> searchFileByName(String filename, String ext, int limit, Predicate<String> cachekeyfilter);

	public void invalidate(String filename);

	public void refresh();
}
