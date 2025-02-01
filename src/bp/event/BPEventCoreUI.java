package bp.event;

import bp.res.BPResource;
import bp.script.BPScript;
import bp.task.BPTask;

public class BPEventCoreUI extends BPEvent
{
	public final static String EVENTKEY_COREUI_REFRESHPROJECTTREE = "COREUI_R_PRJTREE";
	public final static String EVENTKEY_COREUI_REFRESHPATHTREE = "COREUI_R_PATHTREE";
	public final static String EVENTKEY_COREUI_CHANGETASKSTATUS = "COREUI_C_TASKSTATUS";
	public final static String EVENTKEY_COREUI_CHANGETASKEND = "COREUI_C_TASKEND";
	public final static String EVENTKEY_COREUI_CHANGETASK = "COREUI_C_TASK";
	public final static String EVENTKEY_COREUI_CHANGESCRIPT = "COREUI_C_SCRIPT";

	public String subkey;
	public Object[] datas;

	public BPEventCoreUI(String key, String subkey, Object[] datas)
	{
		this.key = key;
		this.subkey = subkey;
		this.datas = datas;
	}

	public final static BPEventCoreUI refreshProjectTree(String subkey, Object... datas)
	{
		return new BPEventCoreUI(EVENTKEY_COREUI_REFRESHPROJECTTREE, subkey, datas);
	}

	public final static BPEventCoreUI refreshPathTree(BPResource res, boolean recursive)
	{
		String subkey = null;
		if (res != null && res.isFileSystem())
		{
			subkey = res.getID();
		}
		return new BPEventCoreUI(EVENTKEY_COREUI_REFRESHPATHTREE, subkey, new Object[] { res, recursive });
	}

	public final static BPEventCoreUI taskStatusChanged(BPTask<?> task)
	{
		return new BPEventCoreUI(EVENTKEY_COREUI_CHANGETASKSTATUS, task.getID(), new Object[] { task });
	}
	
	public final static BPEventCoreUI taskEnd(BPTask<?> task)
	{
		return new BPEventCoreUI(EVENTKEY_COREUI_CHANGETASKEND, task.getID(), new Object[] { task });
	}

	public final static BPEventCoreUI taskAdded(BPTask<?> task)
	{
		return new BPEventCoreUI(EVENTKEY_COREUI_CHANGETASK, "add", new Object[] { task });
	}

	public final static BPEventCoreUI taskRemoved(BPTask<?> task)
	{
		return new BPEventCoreUI(EVENTKEY_COREUI_CHANGETASK, "remove", new Object[] { task });
	}

	public final static BPEventCoreUI scriptAdded(BPScript script)
	{
		return new BPEventCoreUI(EVENTKEY_COREUI_CHANGESCRIPT, "add", new Object[] { script });
	}

	public final static BPEventCoreUI scriptRemoved(BPScript script)
	{
		return new BPEventCoreUI(EVENTKEY_COREUI_CHANGESCRIPT, "remove", new Object[] { script });
	}
}
