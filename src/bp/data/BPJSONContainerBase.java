package bp.data;

import java.util.Map;

import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPJSONContainerBase<D extends BPMData> extends BPTextContainerBase implements BPMContainer<D>
{
	@SuppressWarnings("unchecked")
	public D readMData(boolean loadsub)
	{
		String text = readAllText();
		Map<String, Object> mobj = JSONUtil.decode(text);
		BPMData d = ObjUtil.mapToObj2(mobj, false);
		return (D) d;
	}

	public Boolean writeMData(D data, boolean savesub)
	{
		String json;
		if (data instanceof BPSLData)
		{
			json = JSONUtil.encode(((BPSLData) data).getSaveData());
		}
		else
		{
			json = JSONUtil.encode(data.getMappedData());
		}
		if (json != null)
		{
			writeAll(TextUtil.fromString(json, "utf-8"));
		}
		return false;
	}
}
