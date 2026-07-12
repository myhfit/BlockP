package bp.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.context.BPFileContext;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.ObjUtil;

public class BPTaskPackSources extends BPTaskPackFiles
{
	public String getTaskName()
	{
		return "Pack Sources";
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		Object[] ps = (Object[]) m_params;
		if (ps != null && ps.length > 1)
		{
			String[] srcs = (String[]) ps[0];
			String srcbase = (String) ps[1];
			String tar = (String) ps[2];
			String tardir = (String) ps[3];
			rc.put("source", String.join(";", srcs));
			rc.put("sourcebase", srcbase);
			rc.put("target", tar);
			rc.put("targetdir", tardir);
			if (ps.length > 4)
				rc.put("packlist", ObjUtil.toBool(ps[4], false));
		}
		return rc;
	}

	protected String[] remapSources(BPFileContext fcontext, String[] srcs)
	{
		List<String> rc = new ArrayList<>();
		for (String src : srcs)
		{
			BPResourceFileSystem base = (BPResourceFileSystem) fcontext.getRes(src);
			if (base != null && base.isDirectory())
			{
				BPResourceFileSystem selfpath = (BPResourceFileSystem) fcontext.getRes("./");
				BPResource[] subs = base.listResources();
				String srcstr;
				if (selfpath.getFileFullName().equals(base.getFileFullName()))
					srcstr = "";
				else
				{
					srcstr = src;
					if (!(srcstr.endsWith("/") || srcstr.endsWith(File.separator)))
						srcstr = srcstr + "/";
				}
				for (BPResource sub : subs)
				{
					String root = findSourceDir(srcstr + sub.getName(), (BPResourceFileSystem) sub, 3);
					if (root != null)
						rc.add(root);
				}
			}
		}
		return rc.toArray(new String[rc.size()]);
	}

	protected String findSourceDir(String cur, BPResourceFileSystem dir, int level)
	{
		BPResource[] chds = dir.listResources();
		if (chds == null)
			return null;
		for (BPResource chd : chds)
		{
			if (!chd.isLeaf() && "src".equals(chd.getName()))
			{
				return cur + "/" + chd.getName();
			}
		}
		if (level == 0)
			return null;
		for (BPResource chd : chds)
		{
			if (!chd.isLeaf())
			{
				String cc = findSourceDir(cur + "/" + chd.getName(), (BPResourceFileSystem) chd, level - 1);
				if (cc != null)
					return cc;
			}
		}
		return null;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		String srcstr = (String) data.get("source");
		String srcbase = (String) data.get("sourcebase");
		String tarstr = (String) data.get("target");
		String tardir = (String) data.get("targetdir");
		boolean packlist = ObjUtil.toBool(data.get("packlist"), false);

		String[] srcs = srcstr == null ? null : srcstr.split(";");
		m_params = new Object[] { srcs, srcbase, tarstr, tardir != null ? tardir : null, packlist };
	}

	public static class BPTaskFactoryPackSources extends BPTaskFactoryBase<BPTaskPackSources>
	{
		public String getName()
		{
			return "Pack Sources";
		}

		protected BPTaskPackSources createTask()
		{
			return new BPTaskPackSources();
		}

		public Class<? extends BPTask<?>> getInstanceClass()
		{
			return BPTaskPackSources.class;
		}
	}
}
