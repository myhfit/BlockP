package bp.task;

import java.util.Map;

import bp.BPCore;
import bp.context.BPFileContext;
import bp.res.BPResourceDir;
import bp.res.BPResourceFileSystem;
import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.ObjUtil;

public class BPTaskCopyFiles extends BPTaskLocal<Boolean>
{
	public BPTaskCopyFiles()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "Copy Files";
	}

	protected void doStart()
	{
		Object[] ps = (Object[]) m_params;
		String[] srcs = (String[]) ps[0];
		String tar = (String) ps[1];
		boolean createdir = ps.length > 2 ? ObjUtil.toBool(ps[2], false) : false;
		BPFileContext context = (BPFileContext) getContext();
		if (context == null)
			context = BPCore.getFileContext();
		BPResourceFileSystem tres = (BPResourceFileSystem) context.getRes(tar);
		if (tres == null && createdir)
		{
			tres = context.getDir(tar);
			((BPResourceDir) tres).makeDir();
		}
		if (tres == null)
		{
			RuntimeException re = new RuntimeException("target not exist");
			setFailed(re);
			m_future.completeExceptionally(re);
		}
		else if (srcs.length > 0)
		{
			setStarted();
			try
			{
				if (tres.isDirectory() || srcs.length == 1)
				{
					if (tres.isDirectory())
					{
						if (srcs.length > 1)
						{
							int count = srcs.length;
							int c = 0;
							for (String src : srcs)
							{
								BPResourceFileSystem res = (BPResourceFileSystem) context.getRes(src);
								res.copy(tres);
								setProgress((float) ++c / (float) count);
								setProgressText(c + "/" + count);
							}
						}
						else
						{
							((BPResourceFileSystem) context.getRes(srcs[0])).copy(tres);
						}
					}
					else
					{
						if (srcs.length > 1)
						{
							throw new RuntimeException("copy source error");
						}
						else
						{
							BPResourceFileSystem fs = ((BPResourceFileSystem) context.getRes(srcs[0]));
							if (fs.isDirectory())
							{
								throw new RuntimeException("copy source/target error");
							}
							else
							{
								fs.copy(tres);
							}
						}
					}
				}
				else
				{
					throw new RuntimeException("copy target error");
				}
				setCompleted();
				m_future.complete(true);
			}
			catch (RuntimeException re)
			{
				setFailed(re);
				m_future.completeExceptionally(re);
			}
		}
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		Object[] ps = (Object[]) m_params;
		if (ps != null && ps.length > 1)
		{
			String[] srcs = (String[]) ps[0];
			String tar = (String) ps[1];
			rc.put("source", String.join(";", srcs));
			rc.put("target", tar);
			if (ps.length > 2)
				rc.put("createdir", ObjUtil.toBool(ps[2], false));
		}
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		String srcstr = (String) data.get("source");
		String tarstr = (String) data.get("target");
		boolean createdir = ObjUtil.toBool(data.get("createdir"), false);
		String[] srcs = srcstr == null ? null : srcstr.trim().split(";");
		m_params = new Object[] { srcs, tarstr.trim(), createdir };
	}

	public static class BPTaskFactoryCopyFiles extends BPTaskFactoryBase<BPTaskCopyFiles>
	{
		public String getName()
		{
			return "Copy Files";
		}

		protected BPTaskCopyFiles createTask()
		{
			return new BPTaskCopyFiles();
		}

		public Class<? extends BPTask<?>> getTaskClass()
		{
			return BPTaskCopyFiles.class;
		}
	}
}
