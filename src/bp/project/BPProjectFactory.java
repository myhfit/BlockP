package bp.project;

import java.util.List;
import java.util.Map;

import bp.res.BPResourceDir;

public interface BPProjectFactory
{
	BPResourceProject create(String prjtype, BPResourceDir dir, Map<String, String> prjdata);

	Class<? extends BPResourceProject> getProjectClass();

	List<String> getProjectTypes();

	default boolean canHandle(String prjtype)
	{
		return false;
	}

	String getName();
}
