package bp.task;

import java.util.HashMap;
import java.util.Map;

import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.ScriptUtil;
import bp.util.Std;

public class BPTaskRemind extends BPTaskLocal<Boolean>
{
	protected String m_content;

	public BPTaskRemind()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "Remind";
	}

	protected void doStart()
	{
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("params", m_params);
		String content = m_content;
		if (content != null)
		{
			content = ScriptUtil.transContent(content, context);
			Std.info_user(content);
		}
		m_future.complete(true);
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		rc.put("content", m_content);
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		m_content = (String) data.get("content");
	}

	public static class BPTaskFactoryRemind extends BPTaskFactoryBase<BPTaskRemind>
	{
		public String getName()
		{
			return "Remind";
		}

		protected BPTaskRemind createTask()
		{
			return new BPTaskRemind();
		}

		public Class<BPTaskRemind> getTaskClass()
		{
			return BPTaskRemind.class;
		}
	}
}
