package bp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.util.ClassUtil;

public class BPServiceManager
{
	public final static CopyOnWriteArrayList<BPService> S_SERVICES = new CopyOnWriteArrayList<BPService>();

	public final static void add(BPService service)
	{
		S_SERVICES.add(service);
	}

	public final static BPService[] list()
	{
		return S_SERVICES.toArray(new BPService[S_SERVICES.size()]);
	}

	@SuppressWarnings("unchecked")
	public final static <T extends BPService> T get(String name)
	{
		BPService[] services = list();
		for (BPService service : services)
		{
			if (service.getName().equals(name))
				return (T) service;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public final static <T extends BPService> T inVM(String name)
	{
		return (T) inVM(name, BPService.class);
	}

	public final static <T extends BPService> T inVM(String name, Class<T> cls)
	{
		ServiceLoader<T> services = ClassUtil.getExtensionServices(cls);
		for (T service : services)
		{
			if (service.getName().equals(name))

				return service;
		}
		return null;
	}

	public final static <T extends BPService> List<T> inVM(Class<T> cls)
	{
		List<T> rc = new ArrayList<T>();
		ServiceLoader<T> services = ClassUtil.getExtensionServices(cls);
		for (T service : services)
		{
			rc.add(service);
		}
		return rc;
	}

	public final static boolean remove(BPService service)
	{
		return S_SERVICES.remove(service);
	}

	public static void stopAll()
	{
		BPService[] services = list();
		for (BPService service : services)
		{
			service.stop();
		}
	}
}
