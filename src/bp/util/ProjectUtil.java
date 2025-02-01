package bp.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import bp.project.BPProjectFactory;
import bp.project.BPResourceProject;
import bp.res.BPResourceDir;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileSystem;

public class ProjectUtil
{
	public final static BPResourceProject createProjectFromDir(BPResourceDir dir, String name, String path)
	{
		String prjtype = null;
		Map<String, String> prjmap = new HashMap<String, String>();
		BPResourceProject prj = null;
		if (dir.isFileSystem())
		{
			BPResourceFileSystem prjfs = dir.getChild(BPResourceProject.S_FILENAME_PRJ);
			if (prjfs != null && prjfs.isFile())
			{
				BPResourceFile prjf = (BPResourceFile) prjfs;
				String prjdata = prjf.useInputStream((in) ->
				{
					try
					{
						return new String(IOUtil.read(in), "utf-8");
					}
					catch (UnsupportedEncodingException e)
					{
						Std.err(e);
					}
					return null;
				});
				prjmap = TextUtil.getPlainMap(prjdata);
				prjtype = prjmap.get("PROJECTTYPE");
			}
			else
			{
				prjmap = new HashMap<String, String>();
				prjtype = "file";
			}
		}
		if (prjtype != null)
		{
			ServiceLoader<BPProjectFactory> facs = ClassUtil.getExtensionServices(BPProjectFactory.class);
			for (BPProjectFactory fac : facs)
			{
				if (fac.getProjectTypes().contains(prjtype))
				{
					prj = createProject(fac, dir, prjtype, name, path, prjmap);
					break;
				}
			}
			if (prj == null)
			{
				for (BPProjectFactory fac : facs)
				{
					if (fac.canHandle(prjtype))
					{
						prj = createProject(fac, dir, prjtype, name, path, prjmap);
						break;
					}
				}
			}
		}
		return prj;
	}

	protected final static BPResourceProject createProject(BPProjectFactory fac, BPResourceDir dir, String prjtype, String name, String path, Map<String, String> prjmap)
	{
		prjmap.put("name", name);
		prjmap.put("path", path);
		BPResourceProject prj = fac.create(prjtype, dir, prjmap);
		if (prj.canCache())
			prj.startCache();
		return prj;
	}

	public final static String getRelativePath(BPResourceProject project, BPResourceFileSystem res)
	{
		String rc = res.getFileFullName();
		if (project.isFileSystem())
		{
			String prjpath = project.getDir().getFileFullName();
			if (!(prjpath.endsWith("/") || prjpath.endsWith(File.separator)))
				prjpath = prjpath + File.separator;
			if (rc.startsWith(prjpath))
			{
				rc = rc.substring(prjpath.length());
			}
		}
		return rc;
	}
}
