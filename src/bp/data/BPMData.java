package bp.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bp.util.ClassUtil;

public interface BPMData extends BPData
{
	default Map<String, Object> getMappedData()
	{
		return new HashMap<String, Object>();
	}

	default void setMappedData(Map<String, Object> data)
	{

	}

	default BPDataStructure getDataStruture()
	{
		return BPDataStructure.M;
	}

	public static class BPMDataCHMap extends ConcurrentHashMap<String, Object> implements BPMData
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -4908755256334414996L;

		public Map<String, Object> getMappedData()
		{
			return this;
		}

		public void setMappedData(Map<String, Object> data)
		{
			clear();
			putAll(data);
		}
	}

	public static class BPMDataWMap implements BPMData
	{
		public volatile Map<String, Object> m_wrapped;

		public BPMDataWMap(Map<String, Object> map)
		{
			m_wrapped = map;
		}

		public Map<String, Object> getMappedData()
		{
			return m_wrapped;
		}

		public void setMappedData(Map<String, Object> data)
		{
			m_wrapped = data;
		}
	}

	public static class BPMDataWMapOrdered extends BPMDataWMap
	{
		public BPMDataWMapOrdered(Map<String, Object> map)
		{
			super(map);
		}
	}

	public static interface BPMDataReflect extends BPMData
	{
		default Map<String, Object> getMappedData()
		{
			return ClassUtil.getMappedDataReflect(this);
		}

		default void setMappedData(Map<String, Object> data)
		{

		}
	}
}
