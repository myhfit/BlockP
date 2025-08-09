package bp.data;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import bp.config.BPConfig;
import bp.format.BPFormatTreeData;

public class BPTreeDataHolder extends BPDataHolder implements BPTreeDataContainer
{
	public BPTreeData readTreeData()
	{
		return (BPTreeData) m_data;
	}

	public CompletionStage<BPTreeData> readTreeDataAsync()
	{
		return CompletableFuture.supplyAsync(this::readTreeData);
	}

	public Boolean writeTreeData(BPTreeData data)
	{
		BPTreeData d = (BPTreeData) m_data;
		d.setRoot(data.getRoot());
		return true;
	}

	public CompletionStage<Boolean> writeTreeDataAsync(BPTreeData data)
	{
		return CompletableFuture.supplyAsync(() -> writeTreeData(data));
	}

	public static class BPTreeDataHolderFactory implements BPDataContainerFactory
	{
		public boolean canHandle(String format)
		{
			return BPFormatTreeData.FORMAT_TREEDATA.equals(format);
		}

		public String getName()
		{
			return "TreeData";
		}

		@SuppressWarnings("unchecked")
		public <T extends BPDataContainer> T createContainer(BPConfig config)
		{
			BPTreeDataHolder h = new BPTreeDataHolder();
			BPTreeData l = new BPTreeData.BPTreeDataObj();
			l.setRoot(new HashMap<>());
			h.setData(l);
			return (T) h;
		}

		public String getFormat()
		{
			return BPFormatTreeData.FORMAT_TREEDATA;
		}
	}
}
