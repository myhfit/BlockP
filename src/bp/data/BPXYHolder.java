package bp.data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import bp.util.Std;

public class BPXYHolder extends BPDataHolder implements BPXYContainer
{
	protected boolean m_seditable = false;

	public BPXYHolder()
	{
	}

	public void setStructureEditable(boolean flag)
	{
		m_seditable = flag;
	}

	public boolean structureEditable()
	{
		return m_seditable;
	}

	public BPXYData readXYData()
	{
		BPXYData rc = null;
		try
		{
			rc = readXYDataAsync().toCompletableFuture().get();
		}
		catch (InterruptedException e)
		{
			Std.err(e);
		}
		catch (ExecutionException e)
		{
			Std.err(e);
			throw new RuntimeException(e.getCause());
		}
		return rc;
	}

	public CompletionStage<BPXYData> readXYDataAsync()
	{
		Supplier<BPXYData> seg = new XYReadSeg(this);
		return CompletableFuture.supplyAsync(seg);
	}

	public Boolean writeXYData(BPXYData data)
	{
		Boolean rc = null;
		try
		{
			rc = writeXYDataAsync(data).toCompletableFuture().get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			Std.err(e);
		}
		return rc;
	}

	public CompletionStage<Boolean> writeXYDataAsync(BPXYData data)
	{
		Supplier<Boolean> seg = new XYWriteSeg(this, data);
		return CompletableFuture.supplyAsync(seg);
	}

	protected static class XYWriteSeg implements Supplier<Boolean>
	{
		protected volatile BPDataHolder m_holder;
		protected volatile BPXYData m_data;

		public XYWriteSeg(BPDataHolder holder, BPXYData data)
		{
			m_holder = holder;
			m_data = data;
		}

		public Boolean get()
		{
			boolean rc = false;
			if (m_data != null && m_data instanceof BPXYData)
			{
				m_holder.setData(m_data.clone());
				rc = true;
			}
			return rc;
		}
	}

	protected static class XYReadSeg implements Supplier<BPXYData>
	{
		protected BPDataHolder m_holder;

		public XYReadSeg(BPDataHolder holder)
		{
			m_holder = holder;
		}

		public BPXYData get()
		{
			Object data = m_holder.getData();
			BPXYData rc = null;
			if (data != null && data instanceof BPXYData)
			{
				rc = ((BPXYData) data).clone();
			}
			return rc;
		}
	}
}
