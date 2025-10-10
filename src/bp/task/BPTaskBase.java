package bp.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bp.BPCore;
import bp.context.BPContext;
import bp.event.BPEventCoreUI;
import bp.util.LockUtil;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.ThreadUtil;

public abstract class BPTaskBase<V> implements BPTask<V>
{
	protected volatile String m_id;

	protected volatile float m_progress;
	protected volatile String m_progresstext;

	protected volatile CompletableFuture<V> m_future;

	protected volatile Object m_params;
	protected volatile Object m_staticparams;

	protected volatile ExecutorService m_exec;

	protected volatile String m_status;

	protected volatile boolean m_stopflag = false;

	protected volatile String m_name;

	protected volatile boolean m_autoremove = false;

	public final static int COMMON_STATUS_STOPPED = 0;
	public final static int COMMON_STATUS_RUNNING = 1;
	public final static int COMMON_STATUS_COMPLETED = 2;
	public final static int COMMON_STATUS_FAILED = 3;

	protected final static String STATUS_TEXT_STOPPED = "STOPPED";
	protected final static String STATUS_TEXT_RUNNING = "RUNNING";
	protected final static String STATUS_TEXT_COMPLETED = "COMPLETED";
	protected final static String STATUS_TEXT_FAILED = "FAILED";

	protected volatile long m_starttime = 0;

	protected volatile boolean m_isrunning;

	protected ReadWriteLock m_lock = new ReentrantReadWriteLock();
	protected ReadWriteLock m_lockid = new ReentrantReadWriteLock();

	protected volatile int m_mf = 0;

	public Future<V> getFuture()
	{
		return m_future;
	}

	public float getProgress()
	{
		return m_progress;
	}

	public String getProgressText()
	{
		return m_progresstext;
	}

	public void setManagerFlag(int mf)
	{
		m_mf = mf;
	}

	public int getManagerFlag()
	{
		return m_mf;
	}

	public String start()
	{
		if (isRunning())
			return m_id;
		m_stopflag = false;
		m_id = tryGenID();
		m_future = new CompletableFuture<V>();
		try
		{
			getExecutorService().execute(() ->
			{
				boolean isrunning = LockUtil.rwLock(m_lock, true, () ->
				{
					if (m_isrunning)
						return true;
					else
					{
						m_isrunning = true;
						return false;
					}
				});
				if (!isrunning)
				{
					try
					{
						doStart();
					}
					finally
					{
						LockUtil.rwLock(m_lock, true, () -> m_isrunning = false);
						triggerEnd();
					}
				}
			});
		}
		catch (Exception e)
		{
			m_future.completeExceptionally(e);
			Std.err(e);
		}
		return m_id;
	}

	public final void stop()
	{
		m_stopflag = true;
		tryStop();
	}

	protected void tryStop()
	{

	}

	public boolean isRunning()
	{
		return LockUtil.rwLock(m_lock, false, () -> m_isrunning);
	}

	protected abstract void doStart();

	public String getID()
	{
		return m_id;
	}

	public void setID(String id)
	{
		m_id = id;
	}

	protected void setProgress(float v)
	{
		m_progress = v;
	}

	protected void setStarted()
	{
		setProgress(0f);
		setProgressText(null);
		setCommonStatus(COMMON_STATUS_RUNNING);
	}

	protected void setFailed(Throwable e)
	{
		setProgress(0f);
		setProgressText(null);
		setCommonStatus(COMMON_STATUS_FAILED);
		if (e != null)
			setProgressText(e.getMessage());
		triggerStatusChanged();
		triggerEnd();
	}

	protected void setCompleted()
	{
		setCompleted(null);
	}

	protected void setCompleted(String completetext)
	{
		setProgress(1f);
		setProgressText(completetext == null ? getCompleteText() : completetext);
		setCommonStatus(COMMON_STATUS_COMPLETED);
		triggerStatusChanged();
		triggerEnd();
	}

	public boolean isCompleted()
	{
		return STATUS_TEXT_COMPLETED.equals(m_status);
	}

	protected String getCompleteText()
	{
		return null;
	}

	protected void setProgressText(String text)
	{
		m_progresstext = text;
	}

	protected String tryGenID()
	{
		String id = LockUtil.rwLock(m_lockid, false, () -> m_id);
		if (id == null)
		{
			id = LockUtil.rwLock(m_lockid, true, () ->
			{
				if (m_id == null)
					m_id = BPCore.genID(getContext());
				return m_id;
			});
		}
		return id;
	}

	public void setParams(Object params)
	{
		m_params = params;
	}

	protected abstract BPContext getContext();

	protected ExecutorService getExecutorService()
	{
		return m_exec != null ? m_exec : ThreadUtil.getSharedTaskPool();
	}

	public void setExecutorService(ExecutorService exec)
	{
		m_exec = exec;
	}

	public void setCommonStatus(int status)
	{
		String str = null;
		switch (status)
		{
			case COMMON_STATUS_STOPPED:
			{
				str = STATUS_TEXT_STOPPED;
				break;
			}
			case COMMON_STATUS_COMPLETED:
			{
				str = STATUS_TEXT_COMPLETED;
				break;
			}
			case COMMON_STATUS_RUNNING:
			{
				str = STATUS_TEXT_RUNNING;
				break;
			}
			case COMMON_STATUS_FAILED:
			{
				str = STATUS_TEXT_FAILED;
				break;
			}
		}
		setStatus(str);
	}

	public void setStatus(String status)
	{
		m_status = status;
	}

	public void triggerStatusChanged()
	{
		BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.taskStatusChanged(this));
	}

	public void triggerEnd()
	{
		BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.taskEnd(this));
		if (isAutoRemove())
			BPCore.removeTask(this);
	}

	public String getStatus()
	{
		return m_status;
	}

	public String getName()
	{
		return m_name;
	}

	public String toString()
	{
		return "[" + getTaskName() + "]:" + getName();
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", getName());
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		m_name = ObjUtil.toString(data.get("name"));
	}

	public void setAutoRemove(boolean flag)
	{
		m_autoremove = flag;
	}

	public boolean isAutoRemove()
	{
		return m_autoremove;
	}

	public void mergeDynamicParams(Object dynamicps)
	{
		m_staticparams = m_params;
		m_params = mergeDynamicParams(m_params, dynamicps);
	}

	protected Object mergeDynamicParams(Object staticps, Object dynamicps)
	{
		return dynamicps;
	}

	public void clearDynamicParams()
	{
		m_params = m_staticparams;
		m_staticparams = null;
	}
}
