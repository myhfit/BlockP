package bp.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TextUtil
{
	public final static char[] S_ES = new char[] { 'r', '\r', 'n', '\n', 't', '\t', 'a', 7, 'b', '\b', 'f', '\f', 'v', 11, '?', '?', '\\', '\\', '\'', '\'', '\"', '\"' };

	public final static Map<String, String> getPlainMap(String text)
	{
		return getPlainMap(text, false);
	}

	public final static Map<String, String> getPlainMap(String text, boolean needorder)
	{
		String[] lines = text.split("\n");
		Map<String, String> rc = needorder ? new LinkedHashMap<String, String>() : new HashMap<String, String>();
		for (String line : lines)
		{
			int vi = line.indexOf("=");
			String k;
			String v = null;
			if (vi > -1)
			{
				k = line.substring(0, vi).trim();
				v = line.substring(vi + 1).trim();
			}
			else
			{
				k = line.trim();
			}
			if (k.length() > 0)
			{
				rc.put(k, v);
			}
		}
		return rc;
	}

	public final static String fromPlainMap(Map<String, String> map, List<String> keys)
	{
		StringBuilder sb = new StringBuilder();
		if (map != null)
		{
			Collection<String> ks = keys;
			if (ks == null)
			{
				ks = map.keySet();
			}
			boolean flag = false;
			for (String key : ks)
			{
				if (flag)
					sb.append("\n");
				else
					flag = true;
				String value = map.get(key);
				sb.append(key);
				sb.append("=");
				sb.append(value);
			}
		}
		return sb.toString();
	}

	public final static String toString(byte[] bs, String en)
	{
		String rc = null;
		if (bs != null)
		{
			try
			{
				rc = new String(bs, en);
			}
			catch (UnsupportedEncodingException e)
			{
				Std.err(e);
			}
		}
		return rc;
	}

	public final static byte[] fromString(String str, String en)
	{
		byte[] bs = null;
		if (str != null)
		{
			try
			{
				bs = str.getBytes(en);
			}
			catch (UnsupportedEncodingException e)
			{
				Std.err(e);
			}
		}
		return bs;
	}

	public final static boolean checkEmpty(String str)
	{
		return str == null || str.trim().length() == 0;
	}

	public final static boolean checkNotEmpty(String str)
	{
		return str != null && str.trim().length() > 0;
	}

	public final static String eds(String str)
	{
		return eds(str, null);
	}

	public final static String eds(String str, String defaultvalue)
	{
		if (str == null)
			return defaultvalue;
		if (str.trim().length() == 0)
			return defaultvalue;
		return str;
	}

	public final static String[] splitEscapePlainText(String str)
	{
		if (str == null)
			return null;
		List<String> rc = new ArrayList<String>();
		char[] chs = str.toCharArray();
		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		char lastc = 0;
		for (int i = 0; i < chs.length; i++)
		{
			char c = chs[i];
			switch (c)
			{
				case '\"':
				case '\'':
				{
					if (flag)
					{
						if (c == lastc)
						{
							lastc = 0;
							flag = !flag;
						}
						else
						{
						}
					}
					else
					{
						flag = !flag;
						lastc = c;
					}
					sb.append(c);
					break;
				}
				case ',':
				{
					if (!flag)
					{
						rc.add(sb.toString());
						sb.setLength(0);
					}
					else
					{
						sb.append(c);
					}
					break;
				}
				default:
				{
					sb.append(c);
					break;
				}
			}
		}
		if (sb.length() > 0)
			rc.add(sb.toString());
		return rc.toArray(new String[rc.size()]);
	}

	public final static char[] toCStyleChar(String str)
	{
		if (str == null)
			return null;
		char[] rc = new char[str.length() + 1];
		System.arraycopy(str.toCharArray(), 0, rc, 0, str.length());
		return rc;
	}

	public final static List<String> splitTextToList(String text, String sp)
	{
		List<String> rc = new ArrayList<String>();
		if (text != null)
		{
			String[] arr = text.split(sp);
			for (String s : arr)
			{
				rc.add(s);
			}
		}
		return rc;
	}

	public final static Map<String, String> getEXWebContentType(String str)
	{
		Map<String, String> rc = new HashMap<String, String>();
		if (str != null && str.length() > 0)
		{
			String[] strs = str.split(";");
			for (String s : strs)
			{
				int vi = s.indexOf("=");
				if (vi > -1)
				{
					String v = ((vi < s.length() - 1) ? s.substring(vi + 1).trim() : "");
					rc.put(s.substring(0, vi).trim(), v);
				}
				else
				{
					rc.put(s.trim(), null);
				}
			}
		}
		return rc;
	}

	public final static boolean checkSTName(String text)
	{
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			switch (c)
			{
				case '?':
				case '*':
				case '<':
				case '>':
				case '/':
				case '\\':
				case ':':
				case ';':
				case '|':
				case ',':
					return false;
			}
		}
		return true;
	}

	public static String getDisplayText(Object value)
	{
		if (value == null)
			return "[NULL]";
		if (value instanceof String)
		{
			return (String) value;
		}
		else
		{
			return ObjUtil.toString(value);
		}
	}

	public final static int lastIndexOfIgnoreCase(String text, String target, int fromIndex)
	{
		char[] scs = text.toCharArray();
		char[] tcs = target.toLowerCase().toCharArray();
		return lastIndexOfIgnoreCase(scs, 0, scs.length, tcs, 0, tcs.length, fromIndex);
	}

	private static int lastIndexOfIgnoreCase(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset, int targetCount, int fromIndex)
	{
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0)
		{
			return -1;
		}
		if (fromIndex > rightIndex)
		{
			fromIndex = rightIndex;
		}
		if (targetCount == 0)
		{
			return fromIndex;
		}

		int strLastIndex = targetOffset + targetCount - 1;
		char strLastChar = target[strLastIndex];
		int min = sourceOffset + targetCount - 1;
		int i = min + fromIndex;

		startSearchForLastChar: while (true)
		{
			while (i >= min && Character.toLowerCase(source[i]) != strLastChar)
			{
				i--;
			}
			if (i < min)
			{
				return -1;
			}
			int j = i - 1;
			int start = j - (targetCount - 1);
			int k = strLastIndex - 1;

			while (j > start)
			{
				if (Character.toLowerCase(source[j--]) != target[k--])
				{
					i--;
					continue startSearchForLastChar;
				}
			}
			return start - sourceOffset + 1;
		}
	}

	public final static boolean containsText(String src, String target, boolean whole, boolean ignorecase)
	{
		if (src == null)
			return false;
		if (whole)
		{
			if (ignorecase)
				return src.equals(target);
			else
				return src.equalsIgnoreCase(target);
		}
		else
		{
			if (ignorecase)
				return src.toUpperCase().contains(target.toUpperCase());
			else
				return src.contains(target);
		}
	}

	public final static int indexOfIgnoreCase(String text, String target, int fromIndex)
	{
		char[] scs = text.toCharArray();
		char[] tcs = target.toLowerCase().toCharArray();
		return indexOfIgnoreCase(scs, 0, scs.length, tcs, 0, tcs.length, fromIndex);
	}

	private final static int indexOfIgnoreCase(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset, int targetCount, int fromIndex)
	{
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		if (targetCount == 0)
		{
			return fromIndex;
		}

		char first = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++)
		{
			if (Character.toLowerCase(source[i]) != first)
			{
				while (++i <= max && Character.toLowerCase(source[i]) != first)
					;
			}

			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && Character.toLowerCase(source[j]) == target[k]; j++, k++)
					;

				if (j == end)
				{
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

	public final static String join(String[] strs, String sp)
	{
		StringBuilder sb = new StringBuilder();
		int c = strs.length;
		for (int i = 0; i < c; ++i)
		{
			if (i != 0)
				sb.append(sp);
			sb.append(strs[i]);
		}
		return sb.toString();
	}

	public final static String join(List<String> strs, String sp)
	{
		StringBuilder sb = new StringBuilder();
		int c = strs.size();
		for (int i = 0; i < c; ++i)
		{
			if (i != 0)
				sb.append(sp);
			sb.append(strs.get(i));
		}
		return sb.toString();
	}

	public final static String escape(String src)
	{
		char[] es = S_ES;
		StringBuilder sb = new StringBuilder();
		int l = src.length();
		int l2 = es.length;
		int i, j;
		char c;
		for (i = 0; i < l; i++)
		{
			c = src.charAt(i);
			if (c == 0)
				sb.append("\\0");
			else
			{
				for (j = 1; j < l2; j += 2)
				{
					if (c == es[j])
					{
						sb.append("\\" + es[j - 1]);
						break;
					}
				}
				if (j >= l2)
					sb.append(c);
			}
		}
		return sb.toString();
	}

	public final static String escapeToASCII(String src, Function<Character, String> unicodetf)
	{
		char[] es = S_ES;
		StringBuilder sb = new StringBuilder();
		int l = src.length();
		int l2 = es.length;
		int i, j;
		char c;
		boolean flag;
		for (i = 0; i < l; i++)
		{
			c = src.charAt(i);
			flag = false;
			for (j = 1; j < l2; j += 2)
			{
				if (c == es[j])
				{
					sb.append("\\" + es[j - 1]);
					flag = true;
					break;
				}
			}
			if (!flag)
			{
				if (c == 0)
				{
					sb.append("\\0");
				}
				else if (c < 32)
				{
					sb.append("\\x" + fillString(Integer.toString(c, 16), '0', 2));
				}
				else if (c > 127)
				{
					if (unicodetf == null)
					{
						int cp = Character.codePointAt(new char[] { c }, 0);
						sb.append("\\u" + fillString(Integer.toString(cp, 16), '0', 4));
					}
					else
					{
						sb.append(unicodetf.apply(c));
					}
				}
				else
				{
					sb.append(c);
				}
			}

		}
		return sb.toString();
	}

	public final static String unescape(String str)
	{
		char[] es = S_ES;
		StringBuilder sb = new StringBuilder();
		int l = str.length();
		int l2 = es.length;
		int i, j;
		boolean flag;
		for (i = 0; i < l; i++)
		{
			char c = str.charAt(i);
			if (c == '\\' && i < l - 1)
			{
				char cn = str.charAt(i + 1);
				flag = false;
				for (j = 0; j < l2; j += 2)
				{
					if (cn == es[j])
					{
						sb.append(es[j + 1]);
						flag = true;
						break;
					}
				}
				if (flag)
					i++;
				else if (cn >= '0' && cn < '8')
				{
					char cn2 = 0, cn3 = 0;
					int cval = cn - '0';
					if (i < l - 2)
					{
						cn2 = str.charAt(i + 2);
						if (i < l - 3)
							cn3 = str.charAt(i + 3);
					}
					if (cn2 >= '0' && cn2 < '8')
					{
						cval = (cval << 3) + (cn2 - '0');
						i += 2;
						if (cn3 >= '0' && cn3 < '8')
						{
							i++;
							cval = (cval << 3) + (cn2 - '0');
						}
						sb.append((char) cval);
					}
					else
					{
						i++;
						if (cn == '0')
							sb.append('\0');
						else
							sb.append((char) cval);
					}
				}
				else if (cn == 'x' || cn == 'X')
				{
					String cpstr = str.substring(i + 2, i + 4);
					int cp = Integer.parseInt(cpstr, 16);
					i = i + 3;
					sb.append(Character.toChars(cp));
				}
				else if (cn == 'u' || cn == 'U')
				{
					String cpstr = str.substring(i + 2, i + 6);
					int cp = Integer.parseInt(cpstr, 16);
					i = i + 5;
					sb.append(Character.toChars(cp));
				}
				else
				{
					i++;
				}
			}
			else
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public final static String fillString(String str, char c, int targetlen)
	{
		int l = str.length();
		StringBuilder sb = new StringBuilder();
		for (int i = l; i < targetlen; i++)
		{
			sb.append(c);
		}
		sb.append(str);
		return sb.toString();
	}

	public static class EscapeStringScanner
	{
		private String str;
		public int endpos;

		public EscapeStringScanner(String str)
		{
			this.str = str;
		}

		public String scan(int startpos)
		{
			return scan(startpos, '"');
		}

		public String scan(int startpos, char q)
		{
			char[] es = S_ES;
			endpos = -1;
			String s = str;
			int p = startpos;
			StringBuilder sb = new StringBuilder();
			boolean success = false;
			int l = s.length();
			char c, nc;
			int i = p;
			for (; i < l; i++)
			{
				c = s.charAt(i);
				if (c == '\\')
				{
					if (i < (l - 1))
					{
						nc = s.charAt(i + 1);
						for (int j = 0; j < es.length; j++)
						{
							if (es[j] == nc)
							{
								sb.append(es[j + 1]);
								i++;
								break;
							}
						}
					}
				}
				else if (c == q)
				{
					endpos = i;
					success = true;
					break;
				}
				else
				{
					sb.append(c);
				}
			}
			if (success)
				return sb.toString();
			else
				return null;
		}
	}
}
