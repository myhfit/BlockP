package bp.task;

import bp.util.SystemUtil;

public class BPTaskKillProcess extends BPTaskLocal<Boolean>
{
	public BPTaskKillProcess()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "Kill Process";
	}

	protected void doStart()
	{
		String params = (String) m_params;
		if (params != null)
		{
			String pidstr = params.trim();
			try
			{
				long pid = Long.parseLong(pidstr);
				SystemUtil.kill(pid, true, true);
				m_future.complete(true);
				setCompleted();
			}
			catch(Exception e)
			{
				m_future.completeExceptionally(e);
				setFailed(e);
			}
			finally
			{

			}
		}
		else
		{
			setCompleted();
		}
	}
}
