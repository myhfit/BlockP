package bp.compare;

import bp.data.BPXYData;

public class BPDataComparatorXY implements BPDataComparator<BPXYData, BPDataComparatorXY.BPDataCompareResultXY>
{
	public BPDataCompareResultXY[] compare(BPXYData[] datas)
	{
		if (datas == null)
			return null;
		int s = datas.length;
		BPDataCompareResultXY[] rc = new BPDataCompareResultXY[s];
		for (int i = 0; i < s; i++)
			rc[i] = new BPDataCompareResultXY();
		return rc;
	}

	public static class BPDataCompareResultXY extends BPDataComparator.BPDataCompareResult
	{

	}
}
