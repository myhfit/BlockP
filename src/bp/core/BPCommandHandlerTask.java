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
	public final static String CN_TASK_LIST = "TASK_LIST";
	public final static String CN_TASK_START = "TASK_START";
	public final static String CN_TASK_STOP = "TASK_STOP";
	public final static String CN_TASK_DEL = "TASK_DEL";
	public final static String CN_TASK_UPDATE = "TASK_UPDATE";

	public BPCommandHandlerTask()
	{
		m_cmdnames = ObjUtil.makeList(CN_TASK_LIST, CN_TASK_START, CN_TASK_STOP, CN_TASK_DEL, CN_TASK_UPDATE);
	}

	public BPCommandResult call(BPCommand cmd)
	{
		String cmdname = cmd.name.toUpperCase();
		switch (cmdname)
		{
			case CN_TASK_START:
				return BPCommandResult.RUN_B(() -> startTask(cmd.ps));
			// case CN_TASK_LIST:
			// return BPCommandResult.OK("BlockP - Core" + getExtensionInfos() +
			// getPlatformInfos());
			// case CN_ECHO:
			// return BPCommandResult.OK(cmd.ps);
			// case CN_HELP:
			// return BPCommandResult.RUN(() -> showHelp(cmd.ps));
			// case CN_LOADMODULE:
			// return BPCommandResult.RUN_B(() -> loadModule(cmd.ps));
			// case CN_UNLOADMODULE:
			// return BPCommandResult.RUN_B(() -> unloadModule(cmd.ps));
			case CN_TASK_LIST:
				return BPCommandResult.RUN(() -> listTasks(cmd.ps));
		}
		return null;
	}

	protected boolean startTask(Object ps)
	{
//		String cmds=(String)ps;
		List<BPTask<?>> tasks=BPCore.getWorkspaceContext().getTaskManager().listTasks();
		tasks.get(0).getID();
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
		return "taskman";
	}
}