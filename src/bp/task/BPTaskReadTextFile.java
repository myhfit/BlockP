package bp.task;

import java.util.Map;

import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.FileUtil;
import bp.util.TextUtil;

public class BPTaskReadTextFile extends BPTaskLocal<String>
{
	protected volatile String m_filename;
	protected volatile String m_encoding;

	public BPTaskReadTextFile()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "Read Text File";
	}

	protected void doStart()
	{
		String filename = m_filename;
		String encoding = m_encoding;
		if (encoding == null)
			encoding = "utf-8";
		setStarted();
		triggerStatusChanged();
		if (filename == null)
		{
			RuntimeException re = new RuntimeException("filename null");
			setFailed(re);
			m_future.completeExceptionally(re);
		}
		else
		{
			try
			{
				String str = TextUtil.toString(FileUtil.readFile(filename), encoding);
				m_future.complete(str);
				setCompleted();
			}
			catch (Exception e)
			{
				m_future.completeExceptionally(e);
				setFailed(e);
			}
		}
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		rc.put("filename", m_filename);
		rc.put("encoding", m_encoding);
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		m_filename = (String) data.get("filename");
		m_encoding = (String) data.get("encoding");
	}

	public static class BPTaskFactoryReadTextFile extends BPTaskFactoryBase<BPTaskReadTextFile>
	{
		public String getName()
		{
			return "Read Text File";
		}

		protected BPTaskReadTextFile createTask()
		{
			return new BPTaskReadTextFile();
		}

		public Class<BPTaskReadTextFile> getTaskClass()
		{
			return BPTaskReadTextFile.class;
		}
	}
}
