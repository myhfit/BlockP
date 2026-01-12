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
		public final static int BASE = 99;

		public int result;

		public void getCompareResultText(StringBuilder sb)
		{
			switch (result)
			{
				case EQUALS:
					sb.append("EQUALS");
					break;
				case ADD:
					sb.append("ADD");
					break;
				case LOSS:
					sb.append("LOSS");
					break;
				case MODIFIED:
					sb.append("MODIFIED");
					break;
				case BASE:
					sb.append("BASE");
					break;
			}
		}

		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			getCompareResultText(sb);
			return sb.toString();
		}

		public String getResultText(int r)
		{
			switch (r)
			{
				case EQUALS:
					return "EQUALS";
				case ADD:
					return "ADD";
				case LOSS:
					return "LOSS";
				case MODIFIED:
					return "MODIFIED";
				case BASE:
					return "BASE";
			}
			return "";
		}
	}
}
