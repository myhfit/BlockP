package bp.compare;

import bp.data.BPXData;
import bp.data.BPXYData;
import bp.util.CompareUtil.COMPARE_FLAGS;
import static bp.util.CompareUtil.compareValue;

import java.util.List;

public class BPDataComparatorXY implements BPDataComparator<BPXYData, BPDataComparatorXY.BPDataCompareResultXY>
{
	public BPDataCompareResultXY[] compare(BPXYData[] datas)
	{
		if (datas == null)
			return null;
		int s = datas.length;
		BPDataCompareResultXY[] rc = new BPDataCompareResultXY[s];
		for (int i = 0; i < s; i++)
		{
			rc[i] = new BPDataCompareResultXY();
		}
		int[] ms = prepare(datas, rc);
		int st = ms[0];
		int rmax = ms[1];
		BPXYData stdata = datas[st];
		COMPARE_FLAGS flags = new COMPARE_FLAGS();
		boolean[] xr = new boolean[datas.length];
		for (int r = 0; r < rmax; r++)
		{
			boolean[] strow = rc[st].cells[r];
			for (int i = 0; i < datas.length; i++)
			{
				if (i == st)
					continue;
				boolean[] rrow = rc[i].cells[r];
				xr[i] = xr[i] | (!compare(ROW(stdata.getDatas(), r), ROW(datas[i].getDatas(), r), rrow, flags));
				mergeR(strow, rrow);
			}
		}
		for (int i = 0; i < datas.length; i++)
		{
			if (i == st)
				rc[i].result = BPDataCompareResultXY.BASE;
			else
				rc[i].result = xr[i] ? BPDataCompareResultXY.MODIFIED : BPDataCompareResultXY.EQUALS;
		}
		return rc;
	}

	protected final static BPXData ROW(List<BPXData> datas, int r)
	{
		if (datas.size() > r)
		{
			BPXData row = datas.get(r);
			return row;
		}
		return null;
	}

	protected final static Object COL(BPXData row, int c)
	{
		if (row != null && row.length() > c)
			return row.getColValue(c);
		return null;
	}

	protected final static void mergeR(boolean[] rst, boolean[] r)
	{
		for (int i = 0; i < rst.length; i++)
		{
			if (!r[i] && rst[i])
				rst[i] = false;
		}
	}

	protected int[] prepare(BPXYData[] datas, BPDataCompareResultXY[] rs)
	{
		int rmax = 0, cmax = 0;
		int st = 0;

		for (int i = 0; i < datas.length; i++)
		{
			BPXYData data = datas[i];
			int c = data.getColumnNames().length;
			int r = data.getDatas().size();
			cmax = Math.max(cmax, c);
			if (r > rmax)
			{
				rmax = r;
				st = i;
			}
		}
		for (BPDataCompareResultXY result : rs)
		{
			boolean[][] cells = new boolean[rmax][cmax + 1];
			for (int r = 0; r < rmax; r++)
				for (int c = 0; c < cmax + 1; c++)
					cells[r][c] = true;
			result.cells = cells;
		}
		return new int[] { st, rmax, cmax };
	}

	protected final static boolean compare(BPXData row0, BPXData row1, boolean[] result, COMPARE_FLAGS flags)
	{
		int c = Math.max(row0 == null ? 0 : row0.length(), row1 == null ? 0 : row1.length());
		boolean f;
		boolean ft = true;
		for (int i = 0; i < c; i++)
		{
			f = compareValue(COL(row0, i), COL(row1, i), flags);
			ft = ft & f;
			result[i + 1] = f;
		}
		result[0] = ft;
		return ft;
	}

	public static class BPDataCompareResultXY extends BPDataComparator.BPDataCompareResult
	{
		public boolean[][] cells;

		public void getCompareResultText(StringBuilder sb)
		{
			super.getCompareResultText(sb);
			if (cells != null)
			{
				for (int i = 0; i < cells.length; i++)
				{
					boolean[] row = cells[i];
					sb.append("\n");
					for (int j = 0; j < row.length; j++)
					{
						sb.append(row[j] ? " " : "X");
					}
				}
			}
		}
	}
}
