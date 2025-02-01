package bp.util;

import java.util.ArrayList;

//test for a faster StringBuilder
public class SB
{
	private ArrayList<int[]> m_poslist;
	private ArrayList<Character> m_chlist;
	private char[] m_chs;
	private int count;

	public SB(char[] chs, int length)
	{
		m_poslist = new ArrayList<int[]>(length);
		m_chlist = new ArrayList<Character>(length);
		m_chs = chs;
	}

	public void append(int start, int end)
	{
		if (end > start)
		{
			m_poslist.add(new int[] { start, end });
			m_chlist.add(null);
			count += end - start;
		}
	}

	public void append(char c)
	{
		m_poslist.add(null);
		m_chlist.add(c);
		count++;
	}

	public void reset()
	{
		m_poslist.clear();
		m_chlist.clear();
		count = 0;
	}

	public String toString()
	{
		ArrayList<int[]> poslist = m_poslist;
		ArrayList<Character> chlist = m_chlist;
		char[] chs = m_chs;
		char[] rc = new char[count];
		int p = 0;
		int l;
		int c = poslist.size();
		for (int i = 0; i < c; i++)
		{
			int[] pos = poslist.get(i);
			if (pos != null)
			{
				l = pos[1] - pos[0];
				System.arraycopy(chs, pos[0], rc, p, l);
				p += l;
			}
			else
			{
				rc[p] = chlist.get(i);
				p++;
			}
		}
		return new String(rc);
	}
}
