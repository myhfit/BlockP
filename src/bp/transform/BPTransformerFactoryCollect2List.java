package bp.transform;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.data.BPDataConsumer;

public class BPTransformerFactoryCollect2List implements BPTransformerFactory
{
	public String getName()
	{
		return "Collect to List";
	}

	public boolean checkData(Object source)
	{
		if (source == null)
			return false;
		if (source instanceof List)
			return true;
		return false;
	}

	public Collection<String> getFunctionTypes()
	{
		return new CopyOnWriteArrayList<String>(new String[] { TF_TOLIST });
	}

	public BPTransformer<?> createTransformer(String func)
	{
		return new BPTransformerCollect2List();
	}

	public static class BPTransformerCollect2List extends BPDataConsumer.BPDataConsumerCollector<Object> implements BPTransformer<Object>
	{
		protected BPDataConsumer<?> m_output;

		public String getInfo()
		{
			return "Collect to List";
		}

		public void setOutput(BPDataConsumer<?> pipe)
		{
			m_output = pipe;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void finish()
		{
			BPDataConsumer out = m_output;
			if (out != null)
			{
				out.runSegment(() -> out.accept(m_datas));
			}
		}
	}
}