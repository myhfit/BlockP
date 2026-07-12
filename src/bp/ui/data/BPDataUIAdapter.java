package bp.ui.data;

import bp.BPCore.BPPlatform;

public interface BPDataUIAdapter
{
	<C> C getUIForData(Object data);

	boolean canHandle(Object data, Class<?> cls);

	boolean canDeal(Object data, Class<?> cls);

	boolean supportPlatform(BPPlatform platform);
}
