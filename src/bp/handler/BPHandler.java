package bp.handler;

import java.util.List;

import bp.util.ClassUtil;

public interface BPHandler<T>
{
	boolean canHandle(T t);

	@SuppressWarnings("rawtypes")
	default int getHandleLevel(T t)
	{
		int rc = Integer.MIN_VALUE;
		if (canHandle(t))
		{
			List<Class<?>> cc = ClassUtil.getClassChain(getClass());
			rc = 0;
			Class<BPHandler> root = BPHandler.class;
			for (int i = 0; i < cc.size() - 1; i++)
			{
				Class<?> c = cc.get(i);
				if (root.isAssignableFrom(c))
				{
					Boolean b = ClassUtil.callMethod(c, "canHandle", new Class<?>[] { Object.class }, this, true, t);
					if (b != null && b)
					{
						rc = cc.size() - i - 1;
						break;
					}
				}
			}
		}
		return rc;
	}
}