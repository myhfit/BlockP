package bp.transform;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.res.BPResourceFileLocal;
import bp.res.BPResourceIO;
import bp.util.IOUtil;

public class BPTransformerFactoryResourceRead implements BPTransformerFactory
{
	public String getName()
	{
		return "Read Resource";
	}

	public boolean checkData(Object source)
	{
		if (source == null)
			return false;
		if (source instanceof String)
			return true;
		if (source instanceof BPResourceIO)
			return true;
		return false;
	}

	public Collection<String> getFunctionTypes()
	{
		return new CopyOnWriteArrayList<String>(new String[] { TF_TOBYTEARRAY });
	}

	public BPTransformer<?> createTransformer(String func)
	{
		return new BPTransformerResourceRead();
	}

	public static class BPTransformerResourceRead extends BPTransformerBase<Object>
	{
		public String getInfo()
		{
			return "Read Resource";
		}

		protected Object transform(Object t)
		{
			BPResourceIO res = null;
			if (t instanceof String)
			{
				res = new BPResourceFileLocal((String) t);
			}
			else
			{
				res = (BPResourceIO) t;
			}
			if (res.exists())
			{
				return IOUtil.read(res);
			}
			else
			{
				return null;
			}
		}
	}
}