package bp.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import bp.util.ClassUtil;

public class BPClientManager
{
	public final static List<BPClientFactory> list()
	{
		List<BPClientFactory> rc = new ArrayList<BPClientFactory>();
		ServiceLoader<BPClientFactory> facs = ClassUtil.getExtensionServices(BPClientFactory.class);
		if (facs != null)
		{
			for (BPClientFactory fac : facs)
				rc.add(fac);
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	public final static <T extends BPClient> List<T> listByCategory(String cat, Map<String, Object> params)
	{
		ServiceLoader<BPClientFactory> facs = ClassUtil.getExtensionServices(BPClientFactory.class);
		List<T> rc = new ArrayList<T>();
		for (BPClientFactory fac : facs)
		{
			List<String> cats = fac.getCategories();
			if (cats != null && cats.contains(cat))
			{
				rc.add((T) fac.getClient(params));
			}
		}
		return rc;
	}

	public final static List<String> listNameByCategory(String cat)
	{
		ServiceLoader<BPClientFactory> facs = ClassUtil.getExtensionServices(BPClientFactory.class);
		List<String> rc = new ArrayList<String>();
		for (BPClientFactory fac : facs)
		{
			List<String> cats = fac.getCategories();
			if (cats != null && cats.contains(cat))
			{
				rc.add(fac.getName());
			}
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	public final static <T extends BPClient> T get(String name, Map<String, Object> params)
	{
		ServiceLoader<BPClientFactory> facs = ClassUtil.getExtensionServices(BPClientFactory.class);
		T rc = null;
		for (BPClientFactory fac : facs)
		{
			if (fac.getName().equals(name))
			{
				rc = (T) fac.getClient(params);
				break;
			}
		}
		return rc;
	}
}
