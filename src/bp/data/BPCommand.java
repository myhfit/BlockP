package bp.data;

public class BPCommand
{
	public String name;

	public Object ps;

	public final static BPCommand fromText(String text)
	{
		BPCommand rc = null;
		if (text.length() > 0)
		{
			rc = new BPCommand();
			int vi = text.indexOf(" ");
			if (vi > -1)
			{
				rc.name = text.substring(0, vi);
				rc.ps = text.substring(vi + 1);
			}
			else
				rc.name = text;
		}
		return rc;
	}
}
