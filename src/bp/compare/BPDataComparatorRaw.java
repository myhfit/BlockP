package bp.compare;

import java.util.LinkedHashMap;
import java.util.Map;

import bp.data.BPDataContainerRandomAccess;
import bp.env.BPEnvCommon;
import bp.env.BPEnvManager;
import bp.util.ObjUtil;

public class BPDataComparatorRaw implements BPDataComparator<BPDataContainerRandomAccess, BPDataComparatorRaw.BPDataCompareResultRaw>
{
	public BPDataCompareResultRaw[] compare(BPDataContainerRandomAccess[] cons)
	{
		if (cons == null)
			return null;
		int s = cons.length;
		BPDataCompareResultRaw[] rc = new BPDataCompareResultRaw[s];
		long maxlen = 0;
		long[] lens = new long[s];
		for (int i = 0; i < s; i++)
		{
			BPDataContainerRandomAccess con = cons[i];
			long l = con.length();
			lens[i] = l;
			maxlen = Math.max(l, maxlen);
			rc[i] = new BPDataCompareResultRaw();
		}
		long pos = 0;
		int blocksize = ObjUtil.toInt(BPEnvManager.getEnv(BPEnvCommon.ENV_NAME_COMMON).getValue(BPEnvCommon.ENVKEY_RAWIO_BLOCKSIZE), 4096);
		long bend;
		// long[] lastpos = new long[s];
		byte[][] comparebss = new byte[s][];
		for (int i = 0; i < s; i++)
			comparebss[i] = new byte[blocksize];
		while (true)
		{
			bend = pos + blocksize;
			if (bend > maxlen)
				bend = maxlen;
			for (int i = 0; i < s; i++)
			{
				if (rc[i].result == 0)
				{
					if (bend > lens[i])
					{
						rc[i].result = BPDataCompareResult.LOSS;
						// if(lastpos[i]!=0)
						rc[i].mergeSegmentResult(new long[] { pos, Math.min(bend, lens[i]) }, BPDataCompareResult.LOSS);
						comparebss[i] = null;
					}
					else
					{
						cons[i].read(pos, comparebss[i], 0, (int) (Math.min(bend, lens[i]) - pos));
					}
				}
			}
			int[][] segresults = compareSeg(comparebss);
			int countnn = 0;
			int counte = 0;
			for (int i = 0; i < s; i++)
			{
				if (rc[i].result != 0)
					continue;
				countnn++;
				int[] segresult = segresults[i];
				if (segresult == null)
					continue;
				int loopr = 0;
				if (segresult.length == 1)
				{
					if (segresult[0] == 0)
					{
						rc[i].mergeSegmentResult(new long[] { pos, Math.min(bend, lens[i]) }, BPDataCompareResult.EQUALS);
						counte++;
					}
					else
					{
						rc[i].mergeSegmentResult(new long[] { pos, Math.min(bend, lens[i]) }, BPDataCompareResult.MODIFIED);
						loopr = BPDataCompareResult.MODIFIED;
					}
				}
				else
				{
					if (segresult[0] > 0)
					{
						rc[i].mergeSegmentResult(new long[] { pos, pos + segresult[0] }, BPDataCompareResult.EQUALS);
						rc[i].mergeSegmentResult(new long[] { pos + segresult[0], Math.min(bend, lens[i]) }, segresult[1]);
					}
					else
					{
						rc[i].mergeSegmentResult(new long[] { pos, Math.min(bend, lens[i]) }, segresult[1]);
					}
					loopr = segresult[1];
				}
				if (loopr != 0)
					rc[i].result = loopr;
			}
			if (countnn == 0 || counte == 0)
				break;

			if (bend >= maxlen)
			{
				break;
			}
			pos += blocksize;
		}

		for (int i = 0; i < s; i++)
		{
			if (rc[i].result == 0)
			{
				rc[i].result = BPDataCompareResult.EQUALS;
			}
		}
		return rc;
	}

	protected int[][] compareSeg(byte[][] bss)
	{
		int[][] rc = new int[bss.length][];
		int spos = -1;
		byte[] sbs = null;
		byte[] bs = null;
		for (int i = 0; i < bss.length; i++)
		{
			sbs = bss[i];
			if (sbs != null)
			{
				spos = i;
				break;
			}
		}
		if (spos >= 0)
		{
			for (int i = spos + 1; i < bss.length; i++)
			{
				bs = bss[i];
				if (bs != null)
				{
					int[] r = compareBS(sbs, bs);
					rc[i] = r;
					if (rc[spos] == null)
					{
						int[] str = new int[r.length];
						str[0] = 0 - r[0];
						if (r.length > 1)
							str[1] = r[1];
						rc[spos] = str;
					}
				}
			}
		}
		return rc;
	}

	protected int[] compareBS(byte[] bs0, byte[] bs1)
	{
		int f = bs0.length - bs1.length;
		if (f == 0)
		{
			for (int i = 0; i < bs0.length; i++)
			{
				if (bs0[i] != bs1[i])
					return new int[] { i, BPDataCompareResult.MODIFIED };
			}
			return new int[] { 0 };
		}
		else
		{
			return new int[] { f };
		}
	}

	public static class BPDataCompareResultRaw extends BPDataComparator.BPDataCompareResult
	{
		protected Map<long[], Integer> m_segs;
		protected long[] m_lastseg;
		protected int m_lastresult;

		public BPDataCompareResultRaw()
		{
			m_segs = new LinkedHashMap<long[], Integer>();
		}

		public void mergeSegmentResult(long[] addrseg, int result)
		{
			boolean needadd = false;
			if (m_lastseg == null || m_lastresult != result)
			{
				needadd = true;
			}

			if (needadd)
			{
				m_segs.put(addrseg, result);
				m_lastresult = result;
				m_lastseg = addrseg;
			}
			else
			{
				m_lastseg[1] = addrseg[1];
			}
		}

		public Map<long[], Integer> getSegmentResults()
		{
			return m_segs;
		}

		public void getCompareResultText(StringBuilder sb)
		{
			super.getCompareResultText(sb);
			if (m_segs != null)
			{
				for (long[] key : m_segs.keySet())
				{
					sb.append("\n");
					sb.append(key[0] + "-" + key[1]);
					sb.append(":" + getResultText(m_segs.get(key)));
				}
			}
		}
	}
}