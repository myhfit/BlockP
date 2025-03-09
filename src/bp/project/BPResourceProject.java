package bp.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.cache.BPCacheDataFileSystem;
import bp.cache.BPTreeCacheNode;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceParent;

public interface BPResourceProject extends BPResourceParent
{
	public static final String S_FILENAME_PRJ = ".bpprj";

	BPResourceDir getDir();

	void setName(String name);

	boolean containResource(BPResource res);

	BPProjectItemFactory[] getItemFactories();

	String getPath();

	String getProjectKey();

	void refreshByCache(BPTreeCacheNode<BPCacheDataFileSystem> root);

	void save(BPResource res);

	void savePrjFile();

	String getProjectTypeName();

	BPResource wrapResource(BPResource f);

	default ActionResult callAction(String action, Map<String, Object> params)
	{
		return null;
	}

	default boolean canCache()
	{
		return false;
	}

	default void startCache()
	{
	}

	default Map<String, Object> getOverview()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", getName());
		rc.put("prjtype", getProjectTypeName());
		return rc;
	}

	default List<BPResource> getProjectFunctionItems()
	{
		List<BPResource> rc = new ArrayList<BPResource>();
		return rc;
	}

	default Map<String, Object> getStatistics()
	{
		return null;
	}

	default void refreshStatistics()
	{

	}
	
	default void initProjectDatas()
	{
		
	}
	
	default void clearProjectDatas()
	{
		
	}

	public static class ActionResult
	{
		public boolean success;
		public String taskid;
		public String msg;
		public Exception err;

		public final static ActionResult SUCCESS(String _taskid)
		{
			ActionResult rc = new ActionResult();
			rc.success = true;
			rc.taskid = _taskid;
			return rc;
		}

		public final static ActionResult FAIL(String _failreason)
		{
			ActionResult rc = new ActionResult();
			rc.success = false;
			rc.msg = _failreason;
			return rc;
		}

		public final static ActionResult ERR(Exception _err)
		{
			ActionResult rc = new ActionResult();
			rc.success = false;
			rc.err = _err;
			return rc;
		}
	}
}
