package bp.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.data.BPYData.BPYDataArrayList;
import bp.util.ObjUtil;

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
			o.setRoot(ObjUtil.cloneData(getRoot()));
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
			o.setRoot(ObjUtil.cloneData(getRoot()));
			return o;
		}
	}
}
