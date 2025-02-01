package bp.data;

import java.util.concurrent.CompletionStage;

public interface BPDataContainerObj<T> extends BPDataContainer
{
	T readObj();

	CompletionStage<T> readObjAsync();

	Boolean writeObj(T data);

	CompletionStage<Boolean> writeObjAsync(T data);
}
