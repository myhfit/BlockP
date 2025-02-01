package bp.scan;

@SuppressWarnings("unchecked")
public class BPCodeScanner<T extends BPCodeScanner<T>> extends BPTextScanner<T>
{
	protected char[] m_transchs = new char[] { '"' };

	public BPCodeScanner()
	{
		m_transchs = new char[] { '"' };
	}

	public T moveToTextInCode(String text, boolean forward) throws BPTextScanException
	{
		char c;
		int pos = m_pos;
		String str = m_str;
		char[] transchs = m_transchs;
		int l = str.length();
		int d = forward ? 1 : -1;
		char[] tarchs = text.toCharArray();
		int l2 = tarchs.length;
		char fch = tarchs[forward ? 0 : l2 - 1];
		boolean istrans = false;
		char lasttranch = 0;
		int i2;
		boolean hasresult = false;
		for (int i = pos; i < l && i > -1; i += d)
		{
			c = str.charAt(i);
			if (!istrans && c == fch)
			{
				boolean matchflag = true;
				i2 = i + d;
				for (int j = forward ? 0 + d : l2 - 1 + d; j < l2 && j > (-1) && i2 < l && i2 > -1; j += d, i2 += d)
				{
					if (tarchs[j] != str.charAt(i2))
					{
						matchflag = false;
						break;
					}
				}
				if (matchflag)
				{
					m_pos = forward ? i : i2 + 1;
					hasresult = true;
					break;
				}
			}
			if (istrans)
			{
				if (c == lasttranch)
				{
					lasttranch = 0;
					istrans = false;
				}
			}
			else
			{
				for (int j = 0; j < transchs.length; j++)
				{
					if (c == transchs[j])
					{
						lasttranch = c;
						istrans = true;
						break;
					}
				}
			}
		}
		if (!hasresult)
			throw new BPTextScanException();
		return (T) this;
	}
}
