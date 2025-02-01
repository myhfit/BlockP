package bp.transform;

import java.util.Date;

import bp.util.DateUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPTransformerToString extends BPTransformerBase<Object>
{
	protected Object transform(Object t)
	{
		if (t == null)
			return null;
		if (t instanceof byte[])
		{
			return TextUtil.toString((byte[]) t, "utf-8");
		}
		else if (t instanceof Number)
		{
			return ObjUtil.toString(t);
		}
		else if (t instanceof String)
		{
			return t;
		}
		else if (t instanceof Date)
		{
			return DateUtil.formatTime(((Date) t).getTime());
		}
		else if (t.getClass().isPrimitive())
		{
			return ObjUtil.toString(t);
		}
		else
		{
			return ObjUtil.toString(t);
		}
	}

	public String getInfo()
	{
		return "Common to Text";
	}
}
