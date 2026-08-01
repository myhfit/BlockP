package bp.context;

import java.util.List;
import java.util.Map;

import bp.res.BPResourceDir;
import bp.res.BPResourceIO;
import bp.schedule.BPSchedule;
import bp.script.BPScript;
import bp.script.BPScriptManager;
import bp.task.BPTask;
import bp.task.BPTaskManager;

public interface BPWorkspaceContext extends BPFileContext
{
	BPTaskManager getTaskManager();

	BPTaskManager getWorkLoadManager();

	BPScriptManager getScriptManager();

	void saveTasks();

	void loadTasks();

	boolean removeTask(BPTask<?> task);

	Map<String, List<BPSchedule>> loadSchedules();

	void saveSchedules(Map<String, List<BPSchedule>> schedules);

	void loadScripts();

	BPScript saveScript(BPScript oldsc, Map<String, Object> newsc);

	void saveScripts();

	default BPResourceIO getConfigRes(String cfgfilename)
	{
		return getConfigRes(cfgfilename, true);
	}

	BPResourceIO getConfigRes(String cfgfilename, boolean needexist);

	BPResourceDir getConfigDir(String cfgfilename, boolean needexist);
}
