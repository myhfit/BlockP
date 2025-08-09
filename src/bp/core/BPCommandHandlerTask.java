package bp.core;

import java.util.ArrayList;
import java.util.List;

import bp.BPCore;
import bp.data.BPCommand;
import bp.data.BPCommandResult;
import bp.task.BPTask;
import bp.task.BPTaskProxy;
import bp.util.ObjUtil;

public class BPCommandHandlerTask extends BPCommandHandlerBase implements BPCommandHandler
{
	public final static String CN_TASK_LIST = "task_list";
	public final static String CN_TASK_START = "task_start";
	public final static String CN_TASK_STOP = "task_stop";
	public final static String CN_TASK_DEL = "task_del";
	public final static String CN_TASK_UPDATE = "task_update";

	public BPCommandHandlerTask()
	{
		m_cmdnames = ObjUtil.makeList(CN_TASK_LIST, CN_TASK_START, CN_TASK_STOP);//, CN_TASK_DEL, CN_TASK_UPDATE);
	}

	public BPCommandResult call(BPCommand cmd)
	{
		String cmdname = cmd.name.toLowerCase();
		switch (cmdname)
		{
			case CN_TASK_START:
				return BPCommandResult.RUN_B(() -> startTask(getPSStringArr(cmd.ps)));
			case CN_TASK_STOP:
				return BPCommandResult.RUN_B(() -> stopTask(getPSStringArr(cmd.ps)));
			case CN_TASK_LIST:
				return BPCommandResult.RUN(() -> listTasks(cmd.ps));
		}
		return null;
	}

	protected boolean startTask(String[] ps)
	{
		if (ps.length == 0)
			return false;
		String taskname = ps[0];
		if (taskname == null || taskname.length() == 0)
			return false;
		List<BPTask<?>> tasks = BPCore.getWorkspaceContext().getTaskManager().listTasks();
		for (BPTask<?> task : tasks)
		{
			if (taskname.equals(task.getName()))
				task.start();
		}
		return true;
	}

	protected boolean stopTask(String[] ps)
	{
		if (ps.length == 0)
			return false;
		String taskname = ps[0];
		if (taskname == null || taskname.length() == 0)
			return false;
		List<BPTask<?>> tasks = BPCore.getWorkspaceContext().getTaskManager().listTasks();
		for (BPTask<?> task : tasks)
		{
			if (taskname.equals(task.getName()))
				task.stop();
		}
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<BPTask<?>> listTasks(Object ps)
	{
		List<BPTask<?>> tasks = BPCore.listTasks();
		List<BPTask<?>> rc = new ArrayList<BPTask<?>>();
		for (BPTask<?> t : tasks)
		{
			BPTaskProxy<?> p = new BPTaskProxy<>();
			p.copyTaskInfo((BPTask) t);
			rc.add(p);
		}
		return rc;
	}

	public String getName()
	{
		return "TaskManager";
	}
}