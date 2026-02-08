package bp.data;

import java.util.concurrent.CompletionStage;

public interface BPTreeDataContainer extends BPDataContainer
{
	BPTreeData readTreeData();

	CompletionStage<BPTreeData> readTreeDataAsync();

	Boolean writeTreeData(BPTreeData data);

	CompletionStage<Boolean> writeTreeDataAsync(BPTreeData data);

	default void setOrderedMap(boolean flag)
	{

	}
}
