package bp.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPClientFreeCall implements BPClient
{
	protected volatile List<String> m_callablemethods;

	public List<String> getCallableMethods()
	{
		CopyOnWriteArrayList<String> rc;
		if (m_callablemethods == null)
			rc = new CopyOnWriteArrayList<String>();
		else
			rc = new CopyOnWriteArrayList<String>(m_callablemethods);
		return rc;
	}

	@SuppressWarnings("unchecked")
	public <T> T call(String action, Object... params)
	{
		// check
		if (m_callablemethods == null)
			throw new RuntimeException("This service can't be called");
		if (!m_callablemethods.contains(action))
			throw new RuntimeException("This method call is not allowed");
		// end check;

		Method[] ms = getClass().getMethods();
		boolean flag = false;
		T rc = null;
		for (Method m : ms)
		{
			if (action.equals(m.getName()))
			{
				int mc = m.getModifiers();
				if (Modifier.isPublic(mc) && (!Modifier.isStatic(mc)) && m.getParameterCount() == params.length)
				{
					flag = true;
					try
					{
						rc = (T) m.invoke(this, params);
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
					{
						throw new RuntimeException(e);
					}
					break;
				}
			}
		}

		if (flag)
		{
			return rc;
		}
		else
			throw new RuntimeException("Method not found");
	}
}
