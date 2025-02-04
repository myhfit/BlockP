package bp.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.TreeMap;

import bp.util.ClassUtil;

public class BPTransformerManager
{
	public final static Map<String, BPTransformer<?>> getTransformer(Object data, String functiontype)
	{
		Map<String, BPTransformer<?>> rc = new TreeMap<String, BPTransformer<?>>();
		ServiceLoader<BPTransformerFactory> facs = ClassUtil.getServices(BPTransformerFactory.class);
		for (BPTransformerFactory fac : facs)
		{
			if (fac.checkData(data) && fac.getFunctionTypes().contains(functiontype))
			{
				rc.put(fac.getName(), fac.createTransformer(functiontype));
			}
		}
		return rc;
	}

	public final static List<BPTransformerFactory> getTransformerFacs(Object data)
	{
		List<BPTransformerFactory> rc = new ArrayList<BPTransformerFactory>();
		ServiceLoader<BPTransformerFactory> facs = ClassUtil.getServices(BPTransformerFactory.class);
		if (data != null)
		{
			for (BPTransformerFactory fac : facs)
			{
				if (fac.checkData(data))
				{
					rc.add(fac);
				}
			}
		}
		else
		{
			for (BPTransformerFactory fac : facs)
			{
				rc.add(fac);
			}
		}
		return rc;
	}

	public final static BPTransformer<?> getTransformer(String facname, String functiontype)
	{
		ServiceLoader<BPTransformerFactory> facs = ClassUtil.getServices(BPTransformerFactory.class);
		for (BPTransformerFactory fac : facs)
		{
			if (fac.getName().equals(facname))
			{
				return fac.createTransformer(functiontype);
			}
		}
		return null;
	}
}
