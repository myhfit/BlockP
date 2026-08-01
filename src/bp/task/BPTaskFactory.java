package bp.task;

import java.util.Map;

import bp.data.BPInstanceFactory;

public interface BPTaskFactory extends BPInstanceFactory<BPTask<?>>
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	default Class<? extends BPTask<?>> getInstanceRootClass()
	{
		return (Class)BPTask.class;
	}

	default String getCategory()
	{
		return null;
	}

	default String[] getExts()
	{
		return null;
	}

	public static String getFactoryTypeName()
	{
		return "Task";
	}

	public static String getDictClassName()
	{
		return BPTask.class.getName();
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
