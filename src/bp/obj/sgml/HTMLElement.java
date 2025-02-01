package bp.obj.sgml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

public class HTMLElement
{
	public HTMLElementTag tag;
	public List<Object> children = new LinkedList<Object>();
	public Map<String, Object> params;
	protected boolean m_compress = S_COMPRESS;
	protected AtomicReference<String> m_cachedstr = new AtomicReference<String>(null);

	private final static boolean S_COMPRESS = false;

	public HTMLElement(HTMLElementTag _tag)
	{
		this(_tag, null);
	}

	public HTMLElement(HTMLElementTag _tag, Map<String, Object> _params)
	{
		tag = _tag;
		params = _params;
		init();
	}

	protected void init()
	{

	}

	public void addChild(Object child)
	{
		if (children == null)
			children = new LinkedList<Object>();
		children.add(child);
	}

	public void clear()
	{
		children.clear();
		init();
	}

	public void setParam(String key, String value)
	{
		lazyCreateParams();
		params.put(key, value);
	}

	public void setParams(Map<String, Object> ps)
	{
		lazyCreateParams();
		params.putAll(ps);
	}

	public void setParams(Object... ps)
	{
		lazyCreateParams();
		if (ps != null && ps.length > 0)
		{
			int c = ps.length;
			for (int i = 0; i < c; i += 2)
				params.put((String) ps[i], ps[i + 1]);
		}
	}

	protected void lazyCreateParams()
	{
		if (params == null)
			params = new HashMap<String, Object>();
	}

	public void cache()
	{
		m_cachedstr.set(getText());
	}

	public void clearCache()
	{
		m_cachedstr.set(null);
	}

	public String getText()
	{
		String cachedstr = m_cachedstr.get();
		if (cachedstr != null)
			return cachedstr;
		StringBuilder sb = new StringBuilder();
		toText(sb);
		return sb.toString();
	}

	public String toString()
	{
		return getText();
	}

	protected void toText(StringBuilder sb, int level)
	{
		String cachedstr = m_cachedstr.get();
		if (cachedstr != null)
		{
			sb.append(cachedstr);
			return;
		}
		boolean haschild = (children != null && children.size() > 0);
		sb.append(tag.getStart(params, !haschild));

		boolean needS = false;
		for (int i = 0; i < children.size(); i++)
		{
			Object child = children.get(i);
			if (child instanceof HTMLElement)
			{
				if (m_compress || (tag.isNULL() && i == 0))
				{
					((HTMLElement) child).toText(sb, level + 1);
				}
				else
				{
					startIndent(sb, level);
					((HTMLElement) child).toText(sb, level + 1);
					needS = true;
				}
			}
			else
			{
				if (m_compress)
				{
					sb.append(child);
				}
				else
				{
					// startIndent(sb, level);
					sb.append(child);
					needS = false;
				}
			}
		}
		if (!m_compress && (!tag.isNULL()) && children != null && children.size() > 0 && level > 0 && needS)
			stopIndent(sb, level);
		if (haschild || tag.isExplicitEnd())
			sb.append(tag.getEnd());
	}

	protected void startIndent(StringBuilder sb, int level)
	{
		sb.append("\n");
		for (int i = 0; i < level; i++)
		{
			sb.append("\t");
		}
	}

	protected void stopIndent(StringBuilder sb, int level)
	{
		sb.append("\n");
		for (int i = 0; i < level - 1; i++)
		{
			sb.append("\t");
		}
	}

	public void toText(StringBuilder sb)
	{
		toText(sb, 0);
	}

	public final static String encodeHTMLText(String str, boolean htmltext)
	{
		if (str == null)
			return null;
		int l = str.length();
		if (l == 0)
			return str;

		StringBuffer sb = new StringBuffer(l * 2);
		for (int i = 0; i < l; i++)
		{
			char c = str.charAt(i);
			int cint = c;
			if (cint <= 0xff)
			{
				switch (cint)
				{
					case '"':
						sb.append("&quot;");
						break;
					case '\'':
						sb.append("&apos;");
						break;
					case '&':
						sb.append("&amp;");
						break;
					case '<':
						sb.append("&lt;");
						break;
					case '>':
						sb.append("&gt;");
						break;
					case '\t':
						sb.append("&#09;");
						break;
					case '\r':
						break;
					case '\n':
						sb.append(htmltext ? "<br>" : "&#13;");
						break;
					case '\f':
						break;
					case ' ':
						sb.append(htmltext ? "&nbsp;" : "&#32;");
						break;
					default:
						sb.append(c);
						break;
				}
			}
			else
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static class StandardElement extends HTMLElement
	{
		public final static String WINDOW_LOAD = "onload";
		public final static String WINDOW_UNLOAD = "onunload";
		public final static String WINDOW_RESIZE = "onresize";

		public final static String FORM_BLUR = "onblur";
		public final static String FORM_CHANGE = "onchange";
		public final static String FORM_FOCUS = "onfocus";
		public final static String FORM_SELECT = "onselect";
		public final static String FORM_SUBMIT = "onsubmit";

		public final static String KEY_KEYDOWN = "onkeydown";
		public final static String KEY_KEYPRESS = "onkeypress";
		public final static String KEY_KEYUP = "onkeyup";

		public final static String MOUSE_CLICK = "onclick";
		public final static String MOUSE_DBLCLICK = "ondblclick";
		public final static String MOUSE_DOWN = "onmousedown";
		public final static String MOUSE_MOVE = "onmousemove";
		public final static String MOUSE_OUT = "onmouseout";
		public final static String MOUSE_OVER = "onmouseover";
		public final static String MOUSE_UP = "onmouseup";
		public final static String MOUSE_WHEEL = "onmousewheel";
		public final static String MOUSE_SCROLL = "onscroll";

		private final static String ATTRNAME_DISABLED = "disabled";
		private final static String ATTRVALUE_DISABLED = "disabled";

		public StandardElement(HTMLElementTag tag)
		{
			super(tag);
		}

		public StandardElement(HTMLElementTag tag, Map<String, Object> params)
		{
			super(tag, params);
		}

		public void setStyle(String style)
		{
			setParam("style", style);
		}

		public void setID(String id)
		{
			setParam("id", id);
		}

		public void setClassName(String classname)
		{
			setParam("class", classname);
		}

		public void setDirection(Direction dir)
		{
			setParam("dir", dir.name());
		}

		public void setEnabled(boolean flag)
		{
			if (flag)
			{
				if (params != null && params.containsKey(ATTRNAME_DISABLED))
					params.remove(ATTRNAME_DISABLED);
			}
			else
				setParam(ATTRNAME_DISABLED, ATTRVALUE_DISABLED);
		}

		public void setEventListener(String eventname, String value)
		{
			setParam(eventname, value);
		}

		public void setOnClick(String value)
		{
			setEventListener("onclick", value);
		}

		public static enum Direction
		{
			LTR, RTL
		}
	}

	public static class TextElement extends StandardElement
	{
		public TextElement(HTMLElementTag tag)
		{
			super(tag);
		}

		public TextElement(HTMLElementTag tag, Map<String, Object> params)
		{
			super(tag, params);
		}

		public void setText(String text)
		{
			children.clear();
			addChild(text);
		}
	}

	public final static String[] EMPTY_TAGS = new String[] { "area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr" };

	public final static boolean isEmptyTag(String tag)
	{
		for (String et : EMPTY_TAGS)
		{
			if (et.equals(tag.toLowerCase()))
				return true;
		}
		return false;
	}

	public static enum HTMLElementTag
	{
		NULL(), DOCTYPE("!DOCTYPE html", false), html(true), head(true), body(true), style(true), form(true), img(false), table(true, true), tr(true), td(true), th(true), col(true), input(false), frame(true), iframe(true, true), button(true), canvas(
				true), center(true), a(true), article(true), h1(true), h2(true), h3(true), h4(true), h5(true), h6(true), b(true), br(true), fieldset(
						true), legend(true), label(true, true), p(true), title(true), meta(false), div(true, true), textarea(true, true), link(false), nav(true, true), script(true, true), select(true, true), option(true, true), progress(true, true);

		private boolean m_hasend;
		private boolean m_explicitend;
		private String m_name;

		private final static String START = "<";
		private final static String END = ">";
		private final static String SLASH = "/";
		private final static String EQUALS = "=";
		private final static String QUOTE = "'";

		private HTMLElementTag()
		{
		}

		private HTMLElementTag(boolean hasend)
		{
			this(hasend, false);
		}

		private HTMLElementTag(boolean hasend, boolean explicitend)
		{
			m_name = this.name();
			m_hasend = hasend;
			m_explicitend = explicitend;
		}

		public boolean isNULL()
		{
			return m_name == null;
		}

		public boolean isExplicitEnd()
		{
			return m_explicitend;
		}

		private HTMLElementTag(String name, boolean hasend)
		{
			m_name = name;
			m_hasend = hasend;
		}

		public String getStart(Map<String, Object> params)
		{
			return getStart(params, false);
		}

		public String getStart(Map<String, Object> params, boolean endflag)
		{
			if (m_name == null)
				return "";

			if (params == null || params.size() == 0)
			{
				return START + m_name + END;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(START + m_name);
			for (Entry<String, Object> entry : params.entrySet())
			{
				sb.append(" " + entry.getKey() + EQUALS + QUOTE + entry.getValue() + QUOTE);
			}
			sb.append((endflag && m_hasend && (!m_explicitend)) ? SLASH + END : END);
			return sb.toString();
		}

		public String getEnd()
		{
			if (m_name == null)
				return "";

			return m_hasend ? START + SLASH + m_name + END : "";
		}
	}
}
