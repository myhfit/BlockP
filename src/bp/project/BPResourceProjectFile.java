package bp.project;

import bp.res.BPResource;
import bp.res.BPResourceDir;

public class BPResourceProjectFile extends BPResourceProjectCached
{
	public BPResourceProjectFile(BPResourceDir dir, boolean nocache)
	{
		super(dir, nocache);
	}

	public String getResType()
	{
		return "file project";
	}

	public BPResource wrapResource(BPResource res)
	{
		BPResource rc = null;
		if (res.isFileSystem())
		{
		}
		if (rc == null)
		{
			rc = super.wrapResource(res);
		}
		return rc;
	}

	public String getProjectTypeName()
	{
		return "file";
	}
}