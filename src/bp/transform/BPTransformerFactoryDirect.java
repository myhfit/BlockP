package bp.transform;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPTransformerFactoryDirect implements BPTransformerFactory
{
	public String getName()
	{
		return "Direct";
	}

	public boolean checkData(Object source)
	{
		return true;
	}

	public Collection<String> getFunctionTypes()
	{
		return new CopyOnWriteArrayList<String>(TF_ALL);
	}

	public BPTransformer<?> createTransformer(String func)
	{
		return new BPTransformerDirect();
	}

	protected static class BPTransformerDirect extends BPTransformerBase<Object>
	{
		public String getInfo()
		{
			return "Direct";
		}

		protected Object transform(Object t)
		{
			return t;
		}
	}
}
