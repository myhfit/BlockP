package bp.data;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public interface BPXYDContainer extends BPXYContainer
{
	BPXYDData readXYDData();

	CompletionStage<BPXYDData> readXYDDataAsync(Consumer<BPXYDData> preparecallback);

	Boolean writeXYDData(BPXYDData data);

	CompletionStage<Boolean> writeXYDDataAsync(BPXYDData data, WeakReference<Runnable> setupcallback);
}
