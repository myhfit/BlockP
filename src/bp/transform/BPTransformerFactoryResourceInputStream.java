package bp.transform;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.res.BPResourceIO;
import bp.util.TextUtil;

public class BPTransformerFactoryResourceInputStream implements BPTransformerFactory
{
	public String getName()
	{
		return "To InputStream";
	}

	public boolean checkData(Object source)
	{
		if (source == null)
			return false;
		if (source instanceof byte[])
			return true;
		if (source instanceof String)
			return true;
		if (source instanceof BPResourceIO)
			return true;
		return false;
	}

	public Collection<String> getFunctionTypes()
	{
		return new CopyOnWriteArrayList<String>(new String[] { TF_TOINPUTSTREAM });
	}

	public BPTransformer<?> createTransformer(String func)
	{
		return new BPTransformerResource2InputStream();
	}

	public static class BPTransformerResource2InputStream extends BPTransformerBase<Object>
	{
		public String getInfo()
		{
			return "To InputStream";
		}

		protected Object transform(Object t)
		{
			if (t instanceof String)
			{
				byte[] bs = TextUtil.fromString((String) t, "utf-8");
				return new ByteArrayInputStream(bs);
			}
			else if (t instanceof byte[])
			{
				return new ByteArrayInputStream((byte[]) t);
			}
			else if (t instanceof BPResourceIO)
			{
				BPResourceIO res = (BPResourceIO) t;
				if (res.exists())
					return res.getInputStream();
			}
			return null;
		}
	}
}