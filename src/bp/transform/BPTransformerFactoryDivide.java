package bp.transform;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.data.BPDataConsumer;

public class BPTransformerFactoryDivide implements BPTransformerFactory
{
	public String getName()
	{
		return "Divide";
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
		return new CopyOnWriteArrayList<String>(TF_ALL);
	}

	public BPTransformer<?> createTransformer(String func)
	{
		return new BPTransformerDivide();
	}

	public static class BPTransformerDivide extends BPDataConsumer.BPDataConsumerBase<List<?>> implements BPTransformer<List<?>>
	{
		protected BPDataConsumer<?> m_output;

		public String getInfo()
		{
			return "Divide";
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void accept(List<?> t)
		{
			BPDataConsumer output = m_output;
			if (output != null)
			{
				for (Object subt : t)
					output.accept(subt);
			}
		}

		public void setOutput(BPDataConsumer<?> pipe)
		{
			m_output = pipe;
		}

		public void setup()
		{
			super.setup();
			BPDataConsumer<?> output = m_output;
			if (output != null)
			{
				output.setup();
			}
		}

		public void finish()
		{
			BPDataConsumer<?> output = m_output;
			if (output != null)
			{
				output.finish();
			}
		}

		public void clear()
		{
			BPDataConsumer<?> output = m_output;
			if (output != null)
			{
				output.clear();
			}
			super.clear();
		}
	}
}