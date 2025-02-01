package bp.transform;

import java.util.ArrayList;
import java.util.List;

public class BPTransformerSplitText extends BPTransformerBase<String>
{
	public String getInfo()
	{
		return "Text to List";
	}

	protected Object transform(String t)
	{
		List<String> rc = new ArrayList<String>();
		String[] strs = t.split(",");
		for (String str : strs)
			rc.add(str);
		return rc;
	}
}