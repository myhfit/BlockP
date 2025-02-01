package bp.scan;

import bp.obj.sgml.HTMLElement;
import bp.util.Std;

public class BPHTMLScanner<T extends BPHTMLScanner<T>> extends BPCodeScanner<T>
{
	public BPHTMLScanner()
	{
		m_transchs = new char[] { '"', '\'' };
	}

	public String currentTag()
	{
		int pos = m_pos;
		String str = m_str;
		char c = str.charAt(pos);
		if (Character.isWhitespace(c))
		{
			return null;
		}
		int l = str.length();
		int np = -1;
		for (int i = pos + 1; i < l; i++)
		{
			c = str.charAt(i);
			if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')))
			{
				np = i;
				break;
			}
		}
		return np == -1 ? str.substring(pos) : str.substring(pos, np);
	}

	public int[] currentElement()
	{
		int[] rc = null;
		String str = m_str;
		int pos = m_pos;
		char c = str.charAt(pos);
		int startpos = pos;
		int strlen = str.length();
		if (c == '<')
		{
			BPHTMLScanner<?> ns = new BPHTMLScanner<>();
			ns.setSource(str).setPos(pos + 1);
			try
			{
				ns.moveToFirstNoBlank(true);
				String tag = ns.currentTag();
				if (tag == null)
					return null;
				// Std.debug("start:"+tag);
				if (tag.startsWith("!"))
				{
					if (tag.equals("!--"))
					{
						ns.moveToTextInCode("-->", true);
						// Std.debug("end:"+tag);
						rc = new int[] { startpos, ns.pos() };
					}
					else
					{
						ns.moveToTextInCode(">", true);
						// Std.debug("end:"+tag);
						rc = new int[] { startpos, ns.pos() };
					}
				}
				else if (!HTMLElement.isEmptyTag(tag))
				{
					int sp = ns.pos();
					ns.moveToText(">", true);
					boolean isclose = false;
					ns.moveToFirstNoBlank(false);
					char lc = str.charAt(ns.pos());
					if (lc == '/')
					{
						isclose = true;
					}
					if (!isclose)
					{
						int subpos = ns.pos() + 1;
						while (true)
						{
							if (subpos >= strlen)
								return null;
							BPHTMLScanner<?> sc2 = new BPHTMLScanner<>();
							sc2.setSource(str).setPos(subpos).moveToText("<", true);
							int substart = sc2.pos();
							sc2.movePos(1).moveToFirstNoBlank(true);
							String subtag = sc2.currentTag();
							// Std.debug("subtag:"+subtag);
							if (subtag == null)
								return null;
							if (subtag.toLowerCase().equals("/" + tag))
							{
								sc2.moveToText(">", true);
								rc = new int[] { startpos, sc2.pos() };
								break;
							}
							else
							{
								sc2.setPos(substart);
								int[] subv = sc2.currentElement();
								if (subv == null)
									return null;
								subpos = subv[1] + 1;
							}
						}
					}
					else
					{
						rc = new int[] { startpos, sp };
					}
				}
				else
				{
					rc = new int[] { startpos, ns.pos() };
				}
			}
			catch (BPTextScanException e)
			{
				Std.err(e);
			}
		}
		return rc;
	}
}
