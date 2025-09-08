package bp.ui.data;

import java.util.ServiceLoader;

import bp.util.ClassUtil;

public class BPDataUIManager
{
	public final static <C> C getUIForData(Object data)
	{
		if (data == null)
			return null;
		Class<?> c = data.getClass();
		ServiceLoader<BPDataUIAdapter> adapters = ClassUtil.getServices(BPDataUIAdapter.class);
		for (BPDataUIAdapter a : adapters)
		{
			if (a.canHandle(data, c))
				return a.getUIForData(data);
		}
		for (BPDataUIAdapter a : adapters)
		{
			if (a.canDeal(data, c))
				return a.getUIForData(data);
		}
		return null;
	}
}