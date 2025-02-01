package bp.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import bp.BPCore;
import bp.event.BPEventCoreUI;
import bp.util.ThreadUtil;

public class BPTaskManager
{
	private final List<BPTask<?>> m_tasks = new CopyOnWriteArrayList<BPTask<?>>();
	protected volatile int m_mf;

	public void setManagerFlag(int mf)
	{
		m_mf = mf;
	}

	public void addTask(BPTask<?> task)
	{
		task.setManagerFlag(m_mf);
		m_tasks.add(task);
		BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.taskAdded(task));
	}

	public boolean removeTask(BPTask<?> task)
	{
		boolean flag = m_tasks.remove(task);
		if (flag)
			BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.taskRemoved(task));
		return flag;
	}

	public List<BPTask<?>> listTasks()
	{
		return new ArrayList<BPTask<?>>(m_tasks);
	}

	public List<BPTask<?>> listTasks(String category)
	{
		List<BPTask<?>> tasks = new ArrayList<BPTask<?>>(m_tasks);
		List<BPTask<?>> rc = new ArrayList<BPTask<?>>();
		for (BPTask<?> task : tasks)
		{
			if (category.equals(task.getCategory()))
			{
				rc.add(task);
			}
		}
		return rc;
	}

	public void stopAll()
	{
		for (BPTask<?> task : m_tasks)
		{
			task.stop();
		}
		waitTasks();
	}

	protected void waitTasks()
	{
		try
		{
			ExecutorService es = ThreadUtil.getSharedTaskPool();
			es.shutdown();
			es.awaitTermination(1000, TimeUnit.MICROSECONDS);
		}
		catch (InterruptedException e)
		{
		}
	}
}
