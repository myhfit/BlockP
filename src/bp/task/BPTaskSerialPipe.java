package bp.task;

import bp.task.BPTaskFactory.BPTaskFactoryBase;

public class BPTaskSerialPipe<V> extends BPTaskSerial<V>
{
	public BPTaskSerialPipe()
	{
		m_pstrans = this::tp;
	}

	protected Object tp(Object lastresult)
	{
		return lastresult;
	}

	public static class BPTaskFactorySerialPipe extends BPTaskFactoryBase<BPTaskSerialPipe<?>>
	{
		public String getName()
		{
			return "Serial Pipe";
		}

		protected BPTaskSerialPipe<?> createTask()
		{
			return new BPTaskSerialPipe<Object>();
		}

		@SuppressWarnings("unchecked")
		public Class<? extends BPTask<?>> getTaskClass()
		{
			return (Class<? extends BPTask<?>>) (Class<?>) BPTaskSerialPipe.class;
		}
	}
}
