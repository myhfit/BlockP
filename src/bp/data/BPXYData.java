package bp.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.data.BPXData.BPXDataArray;

public interface BPXYData extends BPData, Cloneable
{
	public final static String EXT_XYDATA = "[XYDATA]";

	List<BPXData> getDatas();

	Class<?>[] getColumnClasses();

	String[] getColumnNames();

	default String[] getColumnLabels()
	{
		return getColumnNames();
	}

	default BPDataStructure getDataStructure()
	{
		return BPDataStructure.XY;
	}

	void setDatas(List<BPXData> datas);

	BPXYData clone();

	default List<Map<String, Object>> toMapList()
	{
		List<Map<String, Object>> rc = new ArrayList<Map<String, Object>>();
		List<BPXData> datas = getDatas();
		if (datas != null && datas.size() > 0)
		{
			String[] cnames = getColumnNames();
			for (BPXData row : datas)
			{
				Map<String, Object> data = new HashMap<String, Object>();
				for (int i = 0; i < cnames.length; i++)
				{
					data.put(cnames[i], row.getColValue(i));
				}
				rc.add(data);
			}
		}
		return rc;
	}

	default Map<String, Object> toMap(BPXData row)
	{
		String[] cnames = getColumnNames();
		Map<String, Object> rc = new HashMap<String, Object>();
		for (int i = 0; i < cnames.length; i++)
		{
			rc.put(cnames[i], row.getColValue(i));
		}
		return rc;
	}

	default BPXYData reList(List<BPXData> rows)
	{
		BPXYDataList rc = new BPXYDataList(this, false);
		rc.setDatas(rows);
		return rc;
	}

	default BPXYData shadowList(String[] cols, int[] lines)
	{
		return new BPXYDataShadow(this, cols, lines);
	}

	final static class XYDataUtil
	{
		public static BPXData cloneData(BPXData data, boolean threadsafe)
		{

			BPXData rc = null;
			if (threadsafe)
			{
				rc = new BPXData.BPXDataList(data.getValues());
			}
			else
			{
				Object[] src = data.getValues();
				Object[] arr = Arrays.copyOf(src, src.length, Object[].class);
				rc = new BPXData.BPXDataArray(arr);
			}
			return rc;
		}

		public static List<BPXData> cloneDatas(List<BPXData> datas)
		{
			return cloneDatas(datas, true);
		}

		public static List<BPXData> cloneDatas(List<BPXData> datas, boolean threadsafe)
		{
			List<BPXData> rc = null;
			new CopyOnWriteArrayList<BPXData>();
			BPXData[] arr = new BPXData[datas.size()];
			int c = datas.size();
			for (int i = 0; i < c; i++)
			{
				arr[i] = cloneData(datas.get(i), threadsafe);
			}
			if (threadsafe)
			{
				rc = new CopyOnWriteArrayList<BPXData>(arr);
			}
			else
			{
				rc = new ArrayList<BPXData>(arr.length);
				if (arr.length > 0)
				{
					Collections.addAll(rc, arr);
				}
			}
			return rc;
		}
	}

	public final static class BPXYDataShadow implements BPXYData
	{
		private BPXYData m_par;
		private String[] m_cns;
		private String[] m_cls;
		private Class<?>[] m_ccs;
		private int[] m_lines;

		private int[] m_colcds;

		public BPXYDataShadow(BPXYData par, String[] cols, int[] lines)
		{
			m_par = par;
			m_lines = lines;
			m_cns = cols;
			m_cls = new String[cols.length];
			m_ccs = new Class<?>[cols.length];
			String[] parcns = par.getColumnNames();
			String[] parcls = par.getColumnLabels();
			Class<?>[] parccs = par.getColumnClasses();
			m_colcds = new int[cols.length];
			for (int i = 0; i < cols.length; i++)
			{
				String c = cols[i];
				int vi = -1;
				for (int j = 0; j < parcns.length; j++)
				{
					if (parcns[j].equals(c))
					{
						vi = j;
						break;
					}
				}
				m_cls[i] = parcls[vi];
				m_ccs[i] = parccs[vi];
				m_colcds[i] = vi;
			}
		}

		public List<BPXData> getDatas()
		{
			List<BPXData> pardatas = m_par.getDatas();
			List<BPXData> rc = new ArrayList<BPXData>();
			int[] lines = m_lines;
			if (lines != null)
			{
				for (int i = 0; i < lines.length; i++)
					rc.add(new BPXDataShadow(pardatas.get(lines[i])));
			}
			else
			{
				for (int i = 0; i < pardatas.size(); i++)
				{
					rc.add(new BPXDataShadow(pardatas.get(i)));
				}
			}
			return rc;
		}

		public Class<?>[] getColumnClasses()
		{
			return m_ccs;
		}

		public String[] getColumnNames()
		{
			return m_cns;
		}

		public String[] getColumnLabels()
		{
			return m_cls;
		}

		public void setDatas(List<BPXData> datas)
		{

		}

		public BPXYData clone()
		{
			Class<?>[] ccs = new Class<?>[m_ccs.length];
			String[] cns = new String[m_cns.length];
			System.arraycopy(m_ccs, 0, ccs, 0, m_ccs.length);
			System.arraycopy(m_cns, 0, cns, 0, m_cns.length);
			String[] cls = null;
			if (m_cls != null)
			{
				cls = new String[m_cls.length];
				System.arraycopy(m_cls, 0, cls, 0, m_cls.length);
			}
			BPXYDataList rc = new BPXYDataList(ccs, cns, cls, null, false);
			rc.m_list = XYDataUtil.cloneDatas(getDatas());
			return rc;
		}

		public final class BPXDataShadow implements BPXData
		{
			private BPXData m_parxdata;

			public BPXDataShadow(BPXData parxdata)
			{
				m_parxdata = parxdata;
			}

			public Object getColValue(int col)
			{
				int c = m_colcds[col];
				return c > -1 ? m_parxdata.getColValue(c) : null;
			}

			public Object[] getValues()
			{
				int[] colcds = m_colcds;
				Object[] rc = new Object[colcds.length];
				for (int i = 0; i < colcds.length; i++)
				{
					int c = colcds[i];
					rc[i] = c > -1 ? m_parxdata.getColValue(c) : null;
				}
				return rc;
			}

			public int length()
			{
				return m_colcds.length;
			}

			public void ensureSize(int size)
			{
				m_parxdata.ensureSize(size);
			}

			public BPXData cloneX(boolean copydata)
			{
				return m_parxdata.cloneX(copydata);
			}
		}
	}

	public final static class BPXYDataList implements BPXYData
	{
		protected volatile List<BPXData> m_list;
		protected volatile String[] m_cns;
		protected volatile Class<?>[] m_ccs;
		protected volatile String[] m_cls;

		public BPXYDataList(boolean singlethread)
		{
			this(null, null, null, null, singlethread);
		}

		public BPXYDataList(BPXYData other, boolean singlethread)
		{
			this(other.getColumnClasses(), other.getColumnNames(), other.getColumnLabels(), other.getDatas(), singlethread);
		}

		public BPXYDataList(Class<?>[] ccs, String[] cns, String[] cls, List<BPXData> datas, boolean singlethread)
		{
			if (singlethread)
			{
				m_list = datas == null ? new ArrayList<BPXData>() : new ArrayList<BPXData>(datas);
			}
			else
			{
				m_list = datas == null ? new CopyOnWriteArrayList<BPXData>() : new CopyOnWriteArrayList<>(datas);
			}
			if (ccs != null)
			{
				m_ccs = new Class[ccs.length];
				System.arraycopy(ccs, 0, m_ccs, 0, ccs.length);
			}
			else
				m_ccs = null;
			if (cns != null)
			{
				m_cns = new String[cns.length];
				System.arraycopy(cns, 0, m_cns, 0, cns.length);
			}
			else
				m_cns = null;
			if (cls != null)
			{
				m_cls = new String[cls.length];
				System.arraycopy(cls, 0, m_cls, 0, cls.length);
			}
			else
			{
				m_cls = null;
			}
		}

		public List<BPXData> getDatas()
		{
			return m_list;
		}

		public Class<?>[] getColumnClasses()
		{
			return m_ccs;
		}

		public String[] getColumnNames()
		{
			return m_cns;
		}

		public String[] getColumnLabels()
		{
			return m_cls != null ? m_cls : m_cns;
		}

		public void setDatas(List<BPXData> datas)
		{
			m_list.clear();
			m_list.addAll(datas);
		}

		public void add(BPXData record)
		{
			m_list.add(record);
		}

		public void fromMapList(List<Map<String, Object>> datas)
		{
			if (datas != null && datas.size() > 0)
			{
				Map<String, Object> data0 = datas.get(0);
				List<String> keys = new ArrayList<String>(data0.keySet());
				if (keys != null)
				{
					int kc = keys.size();
					String[] cns = keys.toArray(new String[kc]);
					String[] cls = keys.toArray(new String[kc]);
					Class<?>[] ccs = new Class[kc];
					for (int i = 0; i < kc; i++)
					{
						ccs[i] = Object.class;
					}
					List<BPXData> xdatas = new ArrayList<BPXData>();
					for (Map<String, Object> data : datas)
					{
						Object[] objs = new Object[kc];
						for (int i = 0; i < kc; i++)
							objs[i] = data.get(cns[i]);
						BPXDataArray xdata = new BPXDataArray(objs);
						xdatas.add(xdata);
					}
					m_cns = cns;
					m_cls = cls;
					m_ccs = ccs;
					setDatas(xdatas);
				}
			}
		}

		public BPXYData clone()
		{
			Class<?>[] ccs = new Class<?>[m_ccs.length];
			String[] cns = new String[m_cns.length];
			System.arraycopy(m_ccs, 0, ccs, 0, m_ccs.length);
			System.arraycopy(m_cns, 0, cns, 0, m_cns.length);
			String[] cls = null;
			if (m_cls != null)
			{
				cls = new String[m_cls.length];
				System.arraycopy(m_cls, 0, cls, 0, m_cls.length);
			}
			BPXYDataList rc = new BPXYDataList(ccs, cns, cls, null, false);
			rc.m_list = XYDataUtil.cloneDatas(m_list);
			return rc;
		}
	}
}
