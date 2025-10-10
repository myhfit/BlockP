package bp.task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.ObjUtil;

public class BPTaskSerial<V> extends BPTaskLocal<V>
{
	protected volatile Queue<BPTask<?>> m_tasks = new ConcurrentLinkedQueue<BPTask<?>>();

	protected volatile Function<Object, Object> m_pstrans = null;

	public BPTaskSerial()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public void addTask(BPTask<?> task)
	{
		m_tasks.add(task);
	}

	@SuppressWarnings("unchecked")
	protected void doStart()
	{
		Queue<BPTask<?>> tasks = new LinkedList<BPTask<?>>(m_tasks);
		int count = tasks.size();
		BPTask<?> task = tasks.poll();
		Object params = m_params;
		Object result = null;
		boolean taskend;
		Exception ex = null;
		setStarted();
		triggerStatusChanged();
		int i = 0;
		while (task != null)
		{
			setProgress((float) i / (float) count);
			setProgressText(i + "/" + count);
			triggerStatusChanged();
			boolean psmerge = params != null;
			if (psmerge)
				task.mergeDynamicParams(params);
			try
			{
				task.start();
				taskend = false;
				while (!taskend)
				{
					try
					{
						result = task.getFuture().get(1, TimeUnit.SECONDS);
						taskend = true;
					}
					catch (TimeoutException e)
					{

					}
					catch (InterruptedException | ExecutionException e)
					{
						ex = e;
						taskend = true;
					}
				}
				if (ex == null)
				{
					if (m_pstrans != null)
						params = m_pstrans.apply(result);
				}
				else
				{
					break;
				}
			}
			finally
			{
				if (psmerge)
					task.clearDynamicParams();
			}
			i++;
			if (m_stopflag)
				break;
			task = tasks.poll();
		}
		if (ex != null)
		{
			m_future.completeExceptionally(ex);
			setFailed(ex);
		}
		else
		{
			setCommonStatus(i == count ? COMMON_STATUS_COMPLETED : COMMON_STATUS_STOPPED);
			m_future.complete((V) result);
		}
		setProgress((float) i / (float) count);
		setProgressText(i + "/" + count);
		triggerStatusChanged();
	}

	public String getTaskName()
	{
		return "Serial";
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		if (m_tasks != null)
		{
			List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
			for (BPTask<?> task : m_tasks)
			{
				tasks.add(task.getSaveData());
			}
			rc.put("tasks", tasks);
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		List<?> taskmos = (List<?>) data.get("tasks");
		if (taskmos != null)
		{
			m_tasks.clear();
			for (Object taskmo : taskmos)
			{
				if (taskmo instanceof BPTask)
					m_tasks.add((BPTask<?>) taskmo);
				else if (taskmo instanceof Map)
					m_tasks.add(ObjUtil.mapToObj((Map<String, Object>) taskmo));
			}
		}
	}

	public static class BPTaskFactorySerial extends BPTaskFactoryBase<BPTaskSerial<?>>
	{
		public String getName()
		{
			return "Serial";
		}

		protected BPTaskSerial<?> createTask()
		{
			return new BPTaskSerial<Object>();
		}

		@SuppressWarnings({ "unchecked" })
		public Class<? extends BPTask<?>> getTaskClass()
		{
			return (Class<? extends BPTask<?>>) (Class<?>) BPTaskSerial.class;
		}
	}
}
