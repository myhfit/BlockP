package bp.util;

//Text Parse Functions
public class TPF
{
	public final static int[] indexOf(String src, String[] ts, int fromIndex)
	{
		int[] rc = new int[2];
		rc[0] = -1;
		rc[1] = -1;
		int srclen = src.length();

		if (fromIndex >= srclen)
		{
			return rc;
		}

		if (fromIndex < 0)
		{
			fromIndex = 0;
		}

		char[] first = new char[ts.length];
		int tslen = ts.length;
		for (int i = 0; i < tslen; i++)
			first[i] = ts[i].charAt(0);

		char[] source = src.toCharArray();
		int srcpos, tarpos, tarlen;
		for (int i = fromIndex; i <= srclen; i++)
		{
			for (int j = 0; j < tslen; j++)
			{
				if (source[i] == first[j])
				{
					String target = ts[j];
					srcpos = i + 1;
					tarpos = 1;
					tarlen = target.length();
					if (tarlen == 1)
					{
						rc[0] = i;
						rc[1] = j;
						return rc;
					}
					while (true)
					{
						if (srcpos >= srclen || tarpos >= tarlen)
							break;
						if (source[srcpos] == target.charAt(tarpos))
						{
							if (tarpos == tarlen - 1)
							{
								rc[0] = i;
								rc[1] = j;
								return rc;
							}
							srcpos++;
							tarpos++;
						}
						else
						{
							break;
						}
					}
				}
			}
		}
		return rc;
	}
}
