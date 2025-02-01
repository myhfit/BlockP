package bp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TemplateUtil
{
	public final static Map<String, Object> bind(Map<String, Object> template, Map<String, Object> data)
	{
		Map<String, Object> env = new HashMap<String, Object>();
		env.put("data", data);
		return bindMap(template, env);
	}

	@SuppressWarnings("unchecked")
	private final static Map<String, Object> bindMap(Map<String, Object> template, Map<String, Object> env)
	{
		Map<String, Object> out = new HashMap<String, Object>();
		for (Entry<String, Object> entry : template.entrySet())
		{
			String k = entry.getKey();
			Object v = entry.getValue();
			if (v != null)
			{
				if (v instanceof String && ((String) v).startsWith("${") && ((String) v).endsWith("}"))
				{
					String vstr = (String) v;
					v = ObjUtil.extract(env, vstr.substring(2, vstr.length() - 1));
				}
				if (v instanceof Map)
				{
					v = bindMap((Map<String, Object>) v, env);
				}
				else if (v instanceof List)
				{
					v = bindList((List<Object>) v, env);
				}
			}
			out.put(k, v);
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	private final static List<Object> bindList(List<Object> template, Map<String, Object> env)
	{
		List<Object> out = new ArrayList<Object>();
		for (int i = 0; i < template.size(); i++)
		{
			Object v = template.get(i);
			boolean selfadd = false;
			if (v != null)
			{
				if (v instanceof String && ((String) v).startsWith("${") && ((String) v).endsWith("}"))
				{
					String vstr = (String) v;
					v = ObjUtil.extract(env, vstr.substring(2, vstr.length() - 1));
					if (v instanceof List)
					{
						v = bindList((List<Object>) v, env);
					}
					else if (v instanceof Map)
					{
						v = bindMap((Map<String, Object>) v, env);
					}
				}
				else
				{
					if (v instanceof List)
					{
						v = bindList((List<Object>) v, env);
					}
					else if (v instanceof Map)
					{
						Map<String, Object> vmap = (Map<String, Object>) v;
						String it = (String) vmap.get("_item_template");
						if (it != null && it.startsWith("${") && it.endsWith("}"))
						{
							selfadd = true;
							String key = it.substring(2, it.length() - 1);
							List<Object> listv = new ArrayList<Object>();
							List<Object> itemobjs = ObjUtil.extract(env, key);
							for (int j = 0; j < itemobjs.size(); j++)
							{
								Object item = itemobjs.get(j);
								Object oldindex = env.put("index", j);
								Object olditem = env.put("item", item);
								listv.add(bindMap(vmap, env));
								env.put("index", oldindex);
								env.put("item", olditem);
							}
							out.addAll(listv);
						}
						else
						{
							v = bindMap((Map<String, Object>) v, env);
						}
					}
				}
			}
			if (!selfadd)
				out.add(v);
		}
		return out;
	}
}
