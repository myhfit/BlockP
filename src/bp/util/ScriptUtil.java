package bp.util;

import java.util.Map;

public class ScriptUtil
{
	public final static String transContent(String content, Map<String, Object> vars)
	{
		StringBuilder sb = new StringBuilder();
		int l = content.length();
		char cn = 0;
		for (int i = 0; i < l; i++)
		{
			char c = content.charAt(i);
			if (c == '$')
			{
				if (i < l - 2)
				{
					cn = content.charAt(i + 1);
					if (cn == '{')
					{
						int vi = content.indexOf('}', i + 2);
						if (vi != -1)
						{
							String varkey = content.substring(i + 2, vi);
							Object v = ObjUtil.extract(vars, varkey);
							sb.append(ObjUtil.toString(v, ""));
							i = vi;
						}
					}
					else if (cn == '$')
					{
						sb.append('$');
						i++;
					}
				}
			}
			else
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
