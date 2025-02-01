package bp.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import bp.util.ClassUtil;

public class BPDataProcessorManager
{
	@SuppressWarnings("rawtypes")
	public final static BPDataProcessor<?, ?> getDataProcessor(String name)
	{
		ServiceLoader<BPDataProcessor> ps = ClassUtil.getExtensionServices(BPDataProcessor.class);
		for (BPDataProcessor<?, ?> p : ps)
		{
			if (name.equals(p.getName()))
			{
				return p;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public final static <T extends BPDataProcessor<?, ?>> T getDataProcessorV(String name)
	{
		BPDataProcessor<?, ?> p = getDataProcessor(name);
		return (T) p;
	}

	@SuppressWarnings("rawtypes")
	public final static List<BPDataProcessor<?, ?>> getDataProcessors(String category)
	{
		List<BPDataProcessor<?, ?>> rc = new ArrayList<BPDataProcessor<?, ?>>();
		ServiceLoader<BPDataProcessor> ps = ClassUtil.getExtensionServices(BPDataProcessor.class);
		for (BPDataProcessor<?, ?> p : ps)
		{
			if (category.equals(p.getCategory()))
			{
				rc.add(p);
			}
		}
		return rc;
	}

	@SuppressWarnings("rawtypes")
	public final static List<String> listCategories()
	{
		List<String> rc = new ArrayList<String>();
		ServiceLoader<BPDataProcessor> ps = ClassUtil.getExtensionServices(BPDataProcessor.class);
		for (BPDataProcessor<?, ?> p : ps)
		{
			String cat = p.getCategory();
			if (cat != null && !rc.contains(cat))
			{
				rc.add(cat);
			}
		}
		return rc;
	}
}
