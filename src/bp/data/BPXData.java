package bp.data;

import java.util.concurrent.CopyOnWriteArrayList;

public interface BPXData extends BPData
{
	Object getColValue(int col);

	default void setColValue(int col, Object v)
	{
	}
	
	default void setColValueOrResize(int col, Object v)
	{
		ensureSize(col + 1);
	}

	void ensureSize(int size);

	default BPDataStructure getDataStructure()
	{
		return BPDataStructure.X;
	}

	Object[] getValues();

	int length();
	
	BPXData cloneX(boolean copydata);

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
		
		public void ensureSize(int size)
		{
			if (values.length < size)
			{
				Object[] vs = new Object[size];
				System.arraycopy(values, 0, vs, 0, values.length);
				values = vs;
			}
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

		public BPXData cloneX(boolean copydata)
		{
			Object[] vs = new Object[] { values.length };
			if (copydata)
				System.arraycopy(values, 0, vs, 0, values.length);
			return new BPXDataArray(vs);
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
		
		public void ensureSize(int size)
		{
			if (values.size() < size)
			{
				Object[] arr = values.toArray();
				Object[] arr2 = new Object[size];
				System.arraycopy(arr, 0, arr2, 0, arr.length);
				values = new CopyOnWriteArrayList<>(arr2);
			}
		}

		public Object[] getValues()
		{
			return values.toArray();
		}

		public int length()
		{
			return values.size();
		}

		public BPXData cloneX(boolean copydata)
		{
			if (copydata)
				return new BPXDataList(getValues());
			else
				return new BPXDataList(new Object[values.size()]);
		}
	}
}
