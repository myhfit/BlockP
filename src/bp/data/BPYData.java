package bp.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BPYData extends BPData
{
	Object getValue(int index);

	default void setValue(int index, Object v)
	{
	}

	Object[] getValues();

	List<Object> getList();

	int length();

	void add(Object item);

	default Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("_list", getList());
		return rc;
	}

	default BPDataStructure getDataStruture()
	{
		return BPDataStructure.Y;
	}

	public abstract static class BPYDataList implements BPYData
	{
		protected List<Object> values;

		public BPYDataList()
		{
			values = createList();
		}

		protected abstract List<Object> createList();

		public Object getValue(int col)
		{
			return values.get(col);
		}

		public void setValue(int col, Object v)
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

		public void add(Object item)
		{
			values.add(item);
		}

		public List<Object> getList()
		{
			return new ArrayList<Object>(values);
		}
	}

	public static class BPYDataArrayList extends BPYDataList
	{
		public BPYDataArrayList()
		{
		}

		public BPYDataArrayList(List<?> datas)
		{
			if (datas != null)
			{
				values.addAll(datas);
			}
		}

		public BPYDataArrayList(Object[] arr)
		{
			if (arr != null)
			{
				for (Object item : arr)
					values.add(item);
			}
		}

		protected List<Object> createList()
		{
			return new ArrayList<Object>();
		}
	}
}
