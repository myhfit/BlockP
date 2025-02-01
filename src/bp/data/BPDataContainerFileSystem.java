package bp.data;

import java.util.function.BiPredicate;

import bp.res.BPResource;

public interface BPDataContainerFileSystem extends BPDataContainer
{
	void readFull(BiPredicate<String, Boolean> filter);

	BPResource[] listResources();
}
