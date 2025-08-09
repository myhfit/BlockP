package bp.data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import bp.config.BPConfig;
import bp.format.BPFormatJSON;
import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.Std;

public class BPJSONContainerBase<D extends BPMData> extends BPTextContainerBase implements BPMContainer<D>, BPTreeDataContainer
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
		try
		{
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
				return writeAllText(json);
			}
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return false;
	}

	public BPTreeData readTreeData()
	{
		String text = readAllText();
		Object mobj = JSONUtil.decode(text);
		BPTreeData rc = new BPTreeData.BPTreeDataObj();
		if (mobj != null)
		{
			if (mobj instanceof List)
			{
				rc = new BPTreeData.BPTreeDataArrayList();
				rc.setRoot(mobj);
			}
			else
			{
				rc.setRoot(mobj);
			}
		}
		return rc;
	}

	public CompletionStage<BPTreeData> readTreeDataAsync()
	{
		return CompletableFuture.supplyAsync(this::readTreeData);
	}

	public Boolean writeTreeData(BPTreeData data)
	{
		String json = null;
		try
		{
			json = JSONUtil.encode(data.getRoot());
			if (json != null)
			{
				return writeAllText(json);
			}
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return false;
	}

	public CompletionStage<Boolean> writeTreeDataAsync(BPTreeData data)
	{
		return null;
	}

	public static class BPJSONContainerFactory implements BPDataContainerFactory
	{
		public boolean canHandle(String format)
		{
			return BPFormatJSON.FORMAT_JSON.equals(format);
		}

		public String getName()
		{
			return "JSON";
		}

		@SuppressWarnings("unchecked")
		public <T extends BPDataContainer> T createContainer(BPConfig config)
		{
			BPJSONContainerBase<BPMData> h = new BPJSONContainerBase<BPMData>();
			return (T) h;
		}

		public String getFormat()
		{
			return BPFormatJSON.FORMAT_JSON;
		}
	}
}
