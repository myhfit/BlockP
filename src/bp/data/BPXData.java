package bp.data;

import java.util.concurrent.CopyOnWriteArrayList;

public interface BPXData extends BPData
{
	Object getColValue(int col);

	default void setColValue(int col, Object v)
	{
	}

	default BPDataStructure getDataStruture()
	{
		return BPDataStructure.X;
	}

	Object[] getValues();

	int length();

	public final static class BPXDataArray implements BPXData
	{
		private volatile Object[] values;

		public BPXDataArray(Object[] values)
		{
			this.values = values;
		}

		public Object getColValue(int col)
		{
			return values[col];
		}

		public void setColValue(int col, Object v)
		{
			values[col] = v;
		}

		public Object[] getValues()
		{
			return values;
		}

		public int length()
		{
			return values.length;
		}
	}

	public final static class BPXDataList implements BPXData
	{
		private volatile CopyOnWriteArrayList<Object> values;

		public BPXDataList(Object[] arr)
		{
			values = new CopyOnWriteArrayList<Object>(arr);
		}

		public Object getColValue(int col)
		{
			return values.get(col);
		}

		public void setColValue(int col, Object v)
		{
			values.set(col, v);
		}

		public Object[] getValues()
		{
			return values.toArray();
		}

		public int length()
		{
			return values.size();
		}
	}
}
