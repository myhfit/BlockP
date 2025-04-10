package bp.task;

import bp.context.BPContext;

public class BPTaskProxy<V> extends BPTaskBase<V> implements BPTask<V>
{
	private String m_taskname;
	private String m_taskclsname;

	public BPTaskProxy()
	{
	}

	public void copyTaskInfo(BPTask<V> task)
	{
		m_taskname = task.getTaskName();
		m_taskclsname = task.getClass().getName();
		m_progress = task.getProgress();
		m_progresstext = task.getProgressText();
		m_name = task.getName();
		m_id = task.getID();
		m_status = task.getStatus();
		m_isrunning = task.isRunning();
	}

	public String getTaskClassName()
	{
		return m_taskclsname;
	}

	public String getTaskName()
	{
		return m_taskname;
	}

	protected void doStart()
	{
	}

	protected BPContext getContext()
	{
		return null;
	}
}
