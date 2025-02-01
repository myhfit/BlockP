package bp.util;

import java.util.HashMap;
import java.util.Map;

public class CommandLineArgs
{
	public String contextpath;
	public Map<String, String> params;

	public CommandLineArgs(String[] args)
	{
		if (args.length > 0)
			contextpath = args[0];
		if (".".equals(contextpath))
			contextpath = null;
		params = new HashMap<String, String>();
		for (int i = 1; i < args.length; i++)
		{
			String arg = args[i];
			int vi = arg.indexOf("=");
			if (vi > -1)
			{
				params.put(arg.substring(0, vi), arg.substring(vi + 1));
			}
			else
			{
				params.put(arg, null);
			}
		}
	}
}
