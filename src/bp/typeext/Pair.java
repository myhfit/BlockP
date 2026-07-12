package bp.typeext;

public interface Pair<L, R>
{
	L getLeft();

	R getRight();

	default String getLeftText()
	{
		Object left = getLeft();
		return left != null ? left.toString() : null;
	}

	default int compareToByLeftText(Pair<?, ?> o)
	{
		String t = getLeftText();
		String t2 = o.getLeftText();
		if (t == null)
			return t2 == null ? 0 : -1;
		if (t2 == null)
			return 1;
		return t.compareToIgnoreCase(t2);
	}

	public static class PairBase<L, R> implements Pair<L, R>
	{
		public L left;
		public R right;

		public PairBase()
		{
		}

		public PairBase(L l, R r)
		{
			left = l;
			right = r;
		}

		public String getLeftText()
		{
			return left != null ? left.toString() : null;
		}

		public L getLeft()
		{
			return left;
		}

		public R getRight()
		{
			return right;
		}

		public String toString()
		{
			return (left != null ? left.toString() : "") + ":" + (right != null ? right.toString() : "");
		}
	}

	public static class Leader<R> extends PairBase<String, R>
	{
		public String toString()
		{
			return left != null ? left.toString() : "";
		}

		public Leader()
		{
		}

		public Leader(String l, R r)
		{
			left = l;
			right = r;
		}
	}
}
