package bp.transform;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPTransformerFactoryCommon implements BPTransformerFactory
{
	public String getName()
	{
		return "Common";
	}

	public boolean checkData(Object source)
	{
		if (source == null)
			return false;
		if (source instanceof byte[] || source instanceof Number || source instanceof Date || source instanceof String || source.getClass().isPrimitive())
			return true;
		return false;
	}

	public Collection<String> getFunctionTypes()
	{
		return new CopyOnWriteArrayList<String>(new String[] { TF_TOSTRING, TF_TOBYTEARRAY });
	}

	public BPTransformer<?> createTransformer(String func)
	{
		if (TF_TOSTRING.equals(func))
			return new BPTransformerToString();
		else if (TF_TOBYTEARRAY.equals(func))
			return new BPTransformerToByteArray();
		return null;
	}

}
