package bp.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.data.BPYData.BPYDataArrayList;

public interface BPTreeData extends BPData
{
	public final static String EXT_TREEDATA = "[TREEDATA]";

	default BPDataStructure getDataStruture()
	{
		return BPDataStructure.T;
	}

	public <T> T getRoot();

	public void setRoot(Object roots);

	BPTreeData clone();

	public class BPTreeDataArrayList extends BPYDataArrayList implements BPTreeData
	{
		public BPDataStructure getDataStruture()
		{
			return BPDataStructure.T;
		}

		@SuppressWarnings("unchecked")
		public <T> T getRoot()
		{
			return (T) getList();
		}

		protected List<Object> createList()
		{
			return new CopyOnWriteArrayList<Object>();
		}

		@SuppressWarnings("unchecked")
		public void setRoot(Object root)
		{
			setRoots((List<Object>) root);
		}

		public void setRoots(List<Object> roots)
		{
			values.clear();
			values.addAll(roots);
		}

		public BPTreeData clone()
		{
			BPTreeDataArrayList o = new BPTreeDataArrayList();
			o.setRoot(TreeDataUtil.cloneData(getRoot()));
			return o;
		}
	}

	public class BPTreeDataObj implements BPTreeData
	{
		protected volatile Object m_data;

		public BPDataStructure getDataStruture()
		{
			return BPDataStructure.T;
		}

		@SuppressWarnings("unchecked")
		public <T> T getRoot()
		{
			return (T) m_data;
		}

		public void setRoot(Object root)
		{
			m_data = root;
		}

		public BPTreeData clone()
		{
			BPTreeDataObj o = new BPTreeDataObj();
			o.setRoot(TreeDataUtil.cloneData(getRoot()));
			return o;
		}
	}

	final static class TreeDataUtil
	{
		@SuppressWarnings("unchecked")
		public final static Object cloneData(Object node)
		{
			if (node == null)
				return null;
			if (node instanceof List)
			{
				List<Object> src = (List<Object>) node;
				List<Object> r = new ArrayList<Object>();
				for (Object s : src)
					r.add(cloneData(s));
				return r;
			}
			else if (node instanceof Map)
			{
				Map<String, Object> kv = (Map<String, Object>) node;
				Map<String, Object> r = new LinkedHashMap<>();
				for (String k : kv.keySet())
					r.put(k, cloneData(kv.get(k)));
				return r;
			}
			return node;
		}
	}
}
