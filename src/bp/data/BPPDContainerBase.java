package bp.data;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import bp.config.BPConfig;
import bp.data.BPTreeData.BPTreeDataArrayList;
import bp.data.BPXYData.BPXYDataList;
import bp.format.BPFormatBPPD;
import bp.res.BPResourceIO;
import bp.util.BPPDUtil;
import bp.util.ObjUtil;
import bp.util.Std;

public class BPPDContainerBase<D extends BPMData> extends BPDataContainerBase implements BPTreeDataContainer,BPXYContainer, BPMContainer<D>
{
	@SuppressWarnings("unchecked")
	public D readMData(boolean loadsub)
	{
		byte[] bs = readAll();
		Map<String, ?> mobj = BPPDUtil.read(bs);
		BPMData d = ObjUtil.mapToObj2(mobj, false);
		return (D) d;
	}

	public Boolean writeMData(D data, boolean savesub)
	{
		if (data instanceof BPSLData)
		{
			return writePDData(((BPSLData) data).getSaveData());
		}
		else
		{
			return writePDData(data.getMappedData());
		}
	}

	public BPTreeData readTreeData()
	{
		byte[] bs = readAll();
		Object mobj = BPPDUtil.read(bs);
		BPTreeData rc = new BPTreeData.BPTreeDataObj();
		if (mobj != null)
		{
			if (mobj instanceof List)
			{
				rc = new BPTreeDataArrayList();
				rc.setRoot(mobj);
			}
			else
				rc.setRoot(mobj);
		}
		return rc;
	}

	public CompletionStage<BPTreeData> readTreeDataAsync()
	{
		return CompletableFuture.supplyAsync(this::readTreeData);
	}

	public Boolean writePDData(Object data)
	{
		try
		{
			BPResourceIO rio = (BPResourceIO) m_res;
			rio.useOutputStream(out ->
			{
				try (BufferedOutputStream bos = new BufferedOutputStream(out))
				{
					BPPDUtil.write(bos, data);
					bos.flush();
				}
				catch (IOException e)
				{
					Std.err(e);
				}
				return true;
			});
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return false;
	}
	
	public Boolean writeTreeData(BPTreeData data)
	{
		return writePDData(data.getRoot());
	}

	public CompletionStage<Boolean> writeTreeDataAsync(BPTreeData data)
	{
		return CompletableFuture.supplyAsync(() -> writeTreeData(data));
	}

	public BPXYData readXYData()
	{
		BPTreeData t = readTreeData();
		List<Map<String, Object>> datas = t.getRoot();
		BPXYDataList rc = new BPXYDataList(false);
		rc.fromMapList(datas);
		return rc;
	}

	public CompletionStage<BPXYData> readXYDataAsync()
	{
		return CompletableFuture.supplyAsync(()->readXYData());
	}

	public Boolean writeXYData(BPXYData data)
	{
		return writePDData(data.toMapList());
	}

	public CompletionStage<Boolean> writeXYDataAsync(BPXYData data)
	{
		return CompletableFuture.supplyAsync(() -> writeXYData(data));
	}

	public static class BPPDContainerFactory implements BPDataContainerFactory
	{
		public boolean canHandle(String format)
		{
			return BPFormatBPPD.FORMAT_BPPD.equals(format);
		}

		public String getName()
		{
			return "BPPD";
		}

		@SuppressWarnings("unchecked")
		public <T extends BPDataContainer> T createContainer(BPConfig config)
		{
			BPPDContainerBase<BPMData> h = new BPPDContainerBase<BPMData>();
			return (T) h;
		}

		public String getFormat()
		{
			return BPFormatBPPD.FORMAT_BPPD;
		}
	}
}
