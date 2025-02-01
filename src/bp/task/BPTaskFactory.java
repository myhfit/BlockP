package bp.task;

import java.util.Map;

public interface BPTaskFactory
{
	BPTask<?> create(Map<String, Object> taskdata);

	String getName();

	Class<? extends BPTask<?>> getTaskClass();

	default String getCategory()
	{
		return null;
	}

	default String[] getExts()
	{
		return null;
	}

	public static abstract class BPTaskFactoryBase<T extends BPTask<?>> implements BPTaskFactory
	{
		protected abstract T createTask();

		public BPTask<?> create(Map<String, Object> taskdata)
		{
			BPTask<?> task = createTask();
			task.setMappedData(taskdata);
			return task;
		}
	}
}
