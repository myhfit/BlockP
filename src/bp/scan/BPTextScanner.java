package bp.scan;

@SuppressWarnings("unchecked")
public class BPTextScanner<T extends BPTextScanner<T>>
{
	protected String m_str;
	protected int m_pos;

	public T setSource(String str)
	{
		m_str = str;
		return (T) this;
	}

	public T setPos(int pos)
	{
		m_pos = pos;
		return (T) this;
	}

	public int pos()
	{
		return m_pos;
	}

	public T movePos(int delta) throws BPTextScanException
	{
		int pos = m_pos + delta;
		if (pos > -1 && pos < m_str.length())
			m_pos = pos;
		else
			throw new BPTextScanException();
		return (T) this;
	}

	public T moveToText(String text, boolean forward) throws BPTextScanException
	{
		int n = forward ? m_str.indexOf(text, m_pos) : m_str.lastIndexOf(text, m_pos);
		if (n != -1)
			m_pos = n;
		else
			throw new BPTextScanException();
		return (T) this;
	}

	public T moveToFirstNoBlank(boolean forward) throws BPTextScanException
	{
		char c;
		int d = forward ? 1 : -1;
		String str = m_str;
		int pos = m_pos;
		int l = str.length();
		int np = -1;
		for (int i = pos; i < l && l > -1; i += d)
		{
			c = str.charAt(i);
			if (!Character.isWhitespace(c))
			{
				np = i;
				break;
			}
		}
		if (np != -1)
			m_pos = np;
		else
			throw new BPTextScanException();
		return (T) this;
	}

	public String currentWord()
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
			if (Character.isWhitespace(c))
			{
				np = i;
				break;
			}
		}
		return np == -1 ? str.substring(pos) : str.substring(pos, np);
	}

	public static class BPTextScanException extends Exception
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 387786721661672511L;

	}
}
