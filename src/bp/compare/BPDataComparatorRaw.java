package bp.compare;

import java.util.LinkedHashMap;
import java.util.Map;

import bp.data.BPDataContainerRandomAccess;
//import bp.env.BPEnvCommon;
//import bp.env.BPEnvManager;
//import bp.util.ObjUtil;

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
//		long pos = 0;
//		int blocksize = ObjUtil.toInt(BPEnvManager.getEnv(BPEnvCommon.ENV_NAME_COMMON).getValue(BPEnvCommon.ENVKEY_RAWIO_BLOCKSIZE), 4096);
//		long bend;
//		while (true)
//		{
//			bend = pos + blocksize;
//			for (int i = 0; i < s; i++)
//			{
//				if (rc[i].result == 0)
//				{
//					if (bend > lens[i])
//					{
//						rc[i].result = BPDataCompareResult.LOSS;
//					}
//				}
//			}
//			if (bend >= maxlen)
//			{
//				break;
//			}
//			pos += blocksize;
//		}

		for (int i = 0; i < s; i++)
		{
			if (rc[i].result == 0)
			{
				rc[i].result = BPDataCompareResult.EQUALS;
			}
		}
		return rc;

	}

	protected Object[] compareSeg(byte[][] bss)
	{
		Object[] rc = new Object[bss.length];
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
					return new int[] { i, 3 };
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

		public BPDataCompareResultRaw()
		{
			m_segs = new LinkedHashMap<long[], Integer>();
		}

		public void addSegmentResult(long[] addrseg, int result)
		{
			m_segs.put(addrseg, result);
		}

		public Map<long[], Integer> getSegmentResults()
		{
			return m_segs;
		}
	}
}