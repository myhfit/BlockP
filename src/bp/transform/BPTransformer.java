package bp.transform;

import bp.data.BPDataConsumer;

public interface BPTransformer<T> extends BPDataConsumer<T>
{
	void setOutput(BPDataConsumer<?> pipe);

	default boolean isTransformer()
	{
		return true;
	}
}
