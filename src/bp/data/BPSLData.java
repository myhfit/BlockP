package bp.data;

import java.util.Map;
import java.util.TreeMap;

public interface BPSLData extends BPMData
{
	public final static String CLSNAME_FIELD = "_classname";

	default Map<String, Object> getSaveData()
	{
		Map<String, Object> rc = new TreeMap<String, Object>();
		rc.put(CLSNAME_FIELD, this.getClass().getName());
		rc.putAll(getMappedData());
		return rc;
	}

	default void setLoadData(Map<String, Object> data)
	{
		data.remove(CLSNAME_FIELD);
		setMappedData(data);
	}
}
