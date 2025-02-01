package bp.transform;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPTransformerFactorySplitText implements BPTransformerFactory
{
	public String getName()
	{
		return "Split Text";
	}

	public boolean checkData(Object source)
	{
		if (source == null)
			return false;
		if (source instanceof String)
			return true;
		return false;
	}

	public Collection<String> getFunctionTypes()
	{
		return new CopyOnWriteArrayList<String>(new String[] { TF_TOLIST });
	}

	public BPTransformer<?> createTransformer(String func)
	{
		return new BPTransformerSplitText();
	}
}
