package bp.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JSONUtil
{
	protected final static NumberFormat s_nfi = new DecimalFormat("#");
	protected final static NumberFormat s_nff = new DecimalFormat("0.######");
	protected final static NumberFormat s_nfd = new DecimalFormat("#");
	protected final static DateTimeFormatter s_ndf= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public final static String testDecodeClassname(String text)
	{
		if (text.startsWith("{\"_classname\":\""))
		{
			int vi = text.indexOf("\"", 15);
			if (vi > -1)
			{
				return text.substring(15, vi);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public final static <T> T decode(String text)
	{
		if (text == null)
			return null;
		Object obj = null;
		int len = text.length();
		for (int i = 0; i < len; i++)
		{
			char c = text.charAt(i);
			if (c != ' ' && c != '\n' && c != '\t' && c != '\r' && c != '\f' && c != '\b' && c != '\0')
			{
				if (c == '{')
				{
					Map<String, Object> newmap = new HashMap<String, Object>();
					innerDecodeObject(text, i + 1, len, newmap);
					obj = newmap;
					break;
				}
				else if (c == '[')
				{
					List<Object> newlist = new ArrayList<Object>();
					innerDecodeArray(text, i + 1, len, newlist);
					obj = newlist;
					break;
				}
				else
				{
					break;
				}
			}
		}
		return (T) obj;
	}

	private final static int innerDecodeArray(String text, int pos, int len, List<Object> list)
	{
		int i = pos;
		int state = 0;// 0:wait value,1:wait comma
		for (; i < len; i++)
		{
			char c = text.charAt(i);
			if (c == '"' && state == 0)
			{
				StringBuilder sb = new StringBuilder();
				i = seekStrEnd(text, i + 1, len, sb);
				String str = sb.toString();
				if (state == 0)
				{
					list.add(str);
				}
			}
			else if (c == '{' && state == 0)
			{
				Map<String, Object> subobj = new HashMap<String, Object>();
				i = innerDecodeObject(text, i + 1, len, subobj);
				list.add(subobj);
				state = 1;
			}
			else if (c == '[' && state == 0)
			{
				List<Object> subarr = new ArrayList<Object>();
				i = innerDecodeArray(text, i + 1, len, subarr);
				list.add(subarr);
				state = 1;
			}
			else if (c == ']')
			{
				return i;
			}
			else if (state == 1 && c == ',')
			{
				state = 0;
			}
			else if (c == ' ' || c == '\t' || c == '\b' || c == '\r' || c == '\n' || c == '\f')
			{
			}
			else if (state == 0)
			{
				if (c == '-' || (c >= '0' && c <= '9'))
				{
					Number r[] = new Number[1];
					i = seekNumberEnd(text, i, len, r);
					list.add(r[0]);
					state = 1;
				}
				else if (c == 't')
				{
					if (text.charAt(i + 1) == 'r' && text.charAt(i + 2) == 'u' && text.charAt(i + 3) == 'e')
					{
						list.add(true);
						state = 1;
					}
				}
				else if (c == 'f')
				{
					if (text.charAt(i + 1) == 'a' && text.charAt(i + 2) == 'l' && text.charAt(i + 3) == 's' && text.charAt(i + 4) == 'e')
					{
						list.add(false);
						state = 1;
					}
				}
				else if (c == 'n')
				{
					if (text.charAt(i + 1) == 'u' && text.charAt(i + 2) == 'l' && text.charAt(i + 3) == 'l')
					{
						list.add(null);
						state = 1;
					}
				}
			}
		}
		return i;
	}

	private final static int innerDecodeObject(String text, int pos, int len, Map<String, Object> obj)
	{
		int i = pos;
		int state = 0;// 1:wait value,2:wait key,3:wait comma
		String key = null;
		for (; i < len; i++)
		{
			char c = text.charAt(i);
			if (c == '"')
			{
				StringBuilder sb = new StringBuilder();
				i = seekStrEnd(text, i + 1, len, sb);
				String str = sb.toString();
				if (state == 0)
				{
					key = str;
					state = 2;
				}
				else if (state == 1)
				{
					obj.put(key, str);
					state = 3;
					key = null;
				}
			}
			else if (c == ':' && state == 2)
			{
				state = 1;
			}
			else if (c == '{' && state == 1)
			{
				Map<String, Object> subobj = new HashMap<String, Object>();
				i = innerDecodeObject(text, i + 1, len, subobj);
				obj.put(key, subobj);
				state = 3;
			}
			else if (c == '[' && state == 1)
			{
				List<Object> subarr = new ArrayList<Object>();
				i = innerDecodeArray(text, i + 1, len, subarr);
				obj.put(key, subarr);
				state = 3;
			}
			else if (c == '}')
			{
				return i;
			}
			else if (state == 3 && c == ',')
			{
				state = 0;
			}
			else if (c == ' ' || c == '\t' || c == '\b' || c == '\r' || c == '\n' || c == '\f')
			{
			}
			else if (state == 1)
			{
				if (c == '-' || (c >= '0' && c <= '9'))
				{
					Number r[] = new Number[1];
					i = seekNumberEnd(text, i, len, r);
					obj.put(key, r[0]);
					key = null;
					state = 3;
				}
				else if (c == 't')
				{
					if (text.charAt(i + 1) == 'r' && text.charAt(i + 2) == 'u' && text.charAt(i + 3) == 'e')
					{
						obj.put(key, true);
						state = 3;
					}
				}
				else if (c == 'f')
				{
					if (text.charAt(i + 1) == 'a' && text.charAt(i + 2) == 'l' && text.charAt(i + 3) == 's' && text.charAt(i + 4) == 'e')
					{
						obj.put(key, false);
						state = 3;
					}
				}
				else if (c == 'n')
				{
					if (text.charAt(i + 1) == 'u' && text.charAt(i + 2) == 'l' && text.charAt(i + 3) == 'l')
					{
						obj.put(key, null);
						state = 3;
					}
				}
			}
		}
		return i;
	}

	private final static int seekNumberEnd(String text, int pos, int len, Number[] r)
	{
		int i = pos;
		int dpos = -1;
		int epos = -1;
		for (; i < len; i++)
		{
			char c = text.charAt(i);
			if (c == '-' || (c >= '0' && c <= '9'))
			{

			}
			else if (c == '.')
			{
				dpos = i;
			}
			else if (c == 'e')
			{
				epos = i;
			}
			else
			{
				break;
			}
		}
		if (i > pos)
		{
			String nt = text.substring(pos, i);
			if (epos > -1)
			{
				nt.replace('e', 'E');
			}
			if (dpos > -1)
			{
				double d = Double.parseDouble(nt);
				r[0] = d;
			}
			else
			{
				try
				{
					long l = Long.parseLong(nt);
					r[0] = l;
				}
				catch (NumberFormatException nfe)
				{
					r[0] = new BigDecimal(nt);
				}
			}
		}
		return i - 1;
	}

	private final static int seekStrEnd(String text, int pos, int len, StringBuilder sb)
	{
		int i = pos;
		char lastc = 0;
		int transs = -1;
		for (; i < len; i++)
		{
			char c = text.charAt(i);
			if (c == '\\')
			{
				transs = i;
				if (i < len - 1)
				{
					char nc = text.charAt(i + 1);
					switch (nc)
					{
						case '"':
						{
							sb.append("\"");
							i++;
							break;
						}
						case '\\':
						{
							sb.append("\\");
							i++;
							break;
						}
						case '/':
						{
							sb.append("/");
							i++;
							break;
						}
						case 'b':
						{
							sb.append("\b");
							i++;
							break;
						}
						case 'f':
						{
							sb.append("\f");
							i++;
							break;
						}
						case 'n':
						{
							sb.append("\n");
							i++;
							break;
						}
						case 'r':
						{
							sb.append("\r");
							i++;
							break;
						}
						case 't':
						{
							sb.append("\t");
							i++;
							break;
						}
						case 'u':
						{
							String ncnum = text.substring(i + 2, i + 6);
							char realc = (char) Integer.parseInt(ncnum, 16);
							i += 5;
							sb.append(realc);
							break;
						}
					}
					lastc = nc;
				}
			}
			else if (c == '"' && (lastc != '\\' || transs != i - 1))
			{
				break;
			}
			else
			{
				sb.append(c);
				lastc = text.charAt(i);
			}
		}
		return i;
	}

	public final static String encode(Object obj)
	{
		return encode(obj, 2);
	}

	public final static String encode(Object obj, int recursivemax)
	{
		StringBuilder sb = new StringBuilder();
		List<Object> recmap = null;
		if (recursivemax > 0)
			recmap = new ArrayList<Object>();
		encode(obj, sb, recursivemax, recmap);
		return sb.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final static void encode(Object obj, StringBuilder sb, int recursivemax, List<Object> recmap)
	{
		if (obj != null)
		{
			if (obj.getClass().isArray())
			{
				if (obj instanceof Object[])
				{
					Object[] c = (Object[]) obj;
					int recnum = 0;
					int recindex = -1;
					if (recursivemax > 0)
					{
						recindex = getRecursiveIndex(recmap, c);
						if (recindex > -1)
							recnum = (Integer) recmap.get(recindex + 1);
						if (recnum == recursivemax)
						{
							sb.append("null");
							return;
						}
						else
						{
							if (recindex > -1)
							{
								recmap.set(recindex + 1, recnum + 1);
							}
							else
							{
								recmap.add(c);
								recmap.add(recnum + 1);
							}
						}
					}
					sb.append('[');
					boolean eflag = false;
					for (Object s : c)
					{
						if (eflag)
							sb.append(",");
						else
							eflag = true;
						encode(s, sb, recursivemax, recmap);
					}
					sb.append(']');
					if (recursivemax > 0)
					{
						if (recindex > -1)
						{
							recmap.set(recindex + 1, ((Integer) recmap.get(recindex + 1)).intValue() - 1);
						}
						else
						{
							int s = recmap.size();
							recmap.remove(s - 1);
							recmap.remove(s - 2);
						}
					}
				}
			}
			else if (obj instanceof Collection)
			{
				Collection c = (Collection) obj;
				int recnum = 0;
				int recindex = -1;
				if (recursivemax > 0)
				{

					recindex = getRecursiveIndex(recmap, c);
					if (recindex > -1)
						recnum = (Integer) recmap.get(recindex + 1);
					if (recnum == recursivemax)
					{
						sb.append("null");
						return;
					}
					else
					{
						if (recindex > -1)
						{
							recmap.set(recindex + 1, recnum + 1);
						}
						else
						{
							recmap.add(c);
							recmap.add(recnum + 1);
						}
					}
				}
				sb.append('[');
				boolean eflag = false;
				for (Object s : c)
				{
					if (eflag)
						sb.append(",");
					else
						eflag = true;
					encode(s, sb, recursivemax, recmap);
				}
				sb.append(']');
				if (recursivemax > 0)
				{
					if (recindex > -1)
					{
						recmap.set(recindex + 1, ((Integer) recmap.get(recindex + 1)).intValue() - 1);
					}
					else
					{
						int s = recmap.size();
						recmap.remove(s - 1);
						recmap.remove(s - 2);
					}
				}
			}
			else if (obj instanceof Map)
			{
				Map<String, ?> m = (Map<String, ?>) obj;
				int recnum = 0;
				int recindex = -1;
				if (recursivemax > 0)
				{

					recindex = getRecursiveIndex(recmap, m);
					if (recindex > -1)
						recnum = (Integer) recmap.get(recindex + 1);
					if (recnum == recursivemax)
					{
						sb.append("null");
						return;
					}
					else
					{
						if (recindex > -1)
						{
							recmap.set(recindex + 1, recnum + 1);
						}
						else
						{
							recmap.add(m);
							recmap.add(recnum + 1);
						}
					}
				}
				sb.append('{');
				boolean eflag = false;
				for (Entry<String, ?> entry : m.entrySet())
				{
					if (eflag)
						sb.append(",");
					else
						eflag = true;
					encode(entry.getKey(), sb, recursivemax, recmap);
					sb.append(':');
					encode(entry.getValue(), sb, recursivemax, recmap);
				}
				sb.append('}');
				if (recursivemax > 0)
				{
					if (recindex > -1)
					{
						recmap.set(recindex + 1, ((Integer) recmap.get(recindex + 1)).intValue() - 1);
					}
					else
					{
						int s = recmap.size();
						recmap.remove(s - 1);
						recmap.remove(s - 2);
					}
				}
			}
			else if (obj instanceof Integer)
			{
				sb.append(Integer.toString((Integer) obj));
			}
			else if (obj instanceof Short)
			{
				sb.append(Short.toString((Short) obj));
			}
			else if (obj instanceof Byte)
			{
				sb.append(Byte.toString((Byte) obj));
			}
			else if (obj instanceof Long)
			{
				sb.append(Long.toString((Long) obj));
			}
			else if (obj instanceof Float)
			{
				sb.append(Float.toString((Float) obj));
			}
			else if (obj instanceof Double)
			{
				sb.append(Double.toString((Double) obj));
			}
			else if (obj instanceof Number)
			{
				sb.append('"');
				q(obj.toString(), sb);
				sb.append('"');
			}
			else if (obj instanceof String)
			{
				sb.append('"');
				q((String) obj, sb);
				sb.append('"');
			}
			else if (obj instanceof Date)
			{
				sb.append('"');
				q(s_ndf.format(((Date)obj).toInstant().atZone(ZoneId.systemDefault())), sb);
				sb.append('"');
			}
			else
			{
				sb.append('"');
				q(obj.toString(), sb);
				sb.append('"');
			}
		}
		else
		{
			sb.append("null");
		}
	}

	private final static int getRecursiveIndex(List<Object> recmap, Object t)
	{
		int len = recmap.size();
		for (int i = 0; i < len; i += 2)
		{
			Object o = recmap.get(i);
			if (o == t)
				return i;
		}
		return -1;
	}

	private static void q(String text, StringBuilder sb)
	{
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			if (c == '\\' || c == '"')
			{
				sb.append('\\');
				sb.append(c);
			}
			else if (c == '\b')
				sb.append("\\b");
			else if (c == '\f')
				sb.append("\\f");
			else if (c == '\n')
				sb.append("\\n");
			else if (c == '\r')
				sb.append("\\r");
			else if (c == '\t')
				sb.append("\\t");
			else
				sb.append(c);
		}
	}

	public final static String unescape(String text)
	{
		if (text == null)
			return null;
		StringBuilder sb = new StringBuilder();
		q(text, sb);
		return sb.toString();
	}

	public final static String escape(String text)
	{
		if (text == null)
			return null;
		StringBuilder sb = new StringBuilder();
		seekStrEnd(text, 0, text.length(), sb);
		return sb.toString();
	}
}