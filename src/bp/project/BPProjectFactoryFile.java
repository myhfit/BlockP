package bp.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.res.BPResourceDir;
import bp.util.ObjUtil;

public class BPProjectFactoryFile implements BPProjectFactory
{
	public BPResourceProject create(String prjtype, BPResourceDir dir, Map<String, String> prjdata)
	{
		BPResourceProjectFile project = new BPResourceProjectFile(dir, ObjUtil.toBool(prjdata.get("nocache"), false));
		if (prjdata.containsKey("name"))
			project.setName(prjdata.get("name"));
		if (prjdata.containsKey("path"))
			project.setPath(prjdata.get("path"));
		return project;
	}

	public Class<? extends BPResourceProject> getProjectClass()
	{
		return BPResourceProjectFile.class;
	}

	public List<String> getProjectTypes()
	{
		List<String> rc = new ArrayList<String>();
		rc.add("file");
		return rc;
	}

	public boolean canHandle(String prjtype)
	{
		return true;
	}

	public String getName()
	{
		return "File Project";
	}
}
