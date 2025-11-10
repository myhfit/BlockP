package bp.transform;

import bp.data.BPDataConsumer;
import bp.data.BPDataConsumer.BPDataConsumerBase;

public abstract class BPTransformerBase<T> extends BPDataConsumerBase<T> implements BPTransformer<T>
{
	protected BPDataConsumer<?> m_output;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void accept(T t)
	{
		BPDataConsumer output = m_output;
		Object nt = transform(t);
		
		if (output != null)
			output.accept(nt);
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

	protected abstract Object transform(T t);

	public void setOutput(BPDataConsumer<?> pipe)
	{
		m_output = pipe;
	}
}
