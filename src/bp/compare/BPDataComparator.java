package bp.compare;

public interface BPDataComparator<D, R extends BPDataComparator.BPDataCompareResult>
{
	R[] compare(D[] datas);

	public static class BPDataCompareResult
	{
		public final static int EQUALS = -1;
		public final static int ADD = 1;
		public final static int LOSS = 2;
		public final static int MODIFIED = 3;

		public int result;
	}
}
