package bp.data;

import java.util.concurrent.CompletionStage;

public interface BPXYContainer extends BPDataContainer
{
	BPXYData readXYData();

	CompletionStage<BPXYData> readXYDataAsync();

	Boolean writeXYData(BPXYData data);

	CompletionStage<Boolean> writeXYDataAsync(BPXYData data);

	default boolean structureEditable()
	{
		return false;
	}
}
