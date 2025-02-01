package bp.task;

import bp.context.BPContext;

public abstract class BPTaskLocal<V> extends BPTaskBase<V>
{
	protected BPContext getContext()
	{
		return null;
	}
}
