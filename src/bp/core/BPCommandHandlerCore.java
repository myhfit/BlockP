package bp.core;

import bp.data.BPCommand;
import bp.data.BPCommandResult;
import bp.module.BPModule;
import bp.module.BPModuleManager;
import bp.util.FileUtil;

public class BPCommandHandlerCore implements BPCommandHandler
{
	public final static String CN_INFO = "CORE_INFO";
	public final static String CN_ECHO = "ECHO";
	public final static String CN_LOADMODULE = "CORE_LOADMODULE";
	public final static String CN_UNLOADMODULE = "CORE_UNLOADMODULE";
	public final static String CN_MODULEINFO = "CORE_MODULEINFO";

	public BPCommandResult call(BPCommand cmd)
	{
		String cmdname = cmd.name.toUpperCase();
		switch (cmdname)
		{
			case CN_INFO:
				return BPCommandResult.OK("BlockP - Core");
			case CN_ECHO:
				return BPCommandResult.OK(cmd.ps);
			case CN_LOADMODULE:
				return BPCommandResult.RUN_B(() -> loadModule(cmd.ps));
			case CN_UNLOADMODULE:
				return BPCommandResult.RUN_B(() -> unloadModule(cmd.ps));
			case CN_MODULEINFO:
				return BPCommandResult.RUN(() -> getModuleInfo(cmd.ps));
		}
		return null;
	}

	protected boolean loadModule(Object ps)
	{
		String filename = (String) ps;
		String cfilename = FileUtil.getContextFileFullName(filename);
		if (cfilename != null)
		{
			Object rc = BPModuleManager.loadModule(cfilename);
			return rc != null;
		}
		return false;
	}

	protected boolean unloadModule(Object ps)
	{
		String name = (String) ps;
		BPModuleManager.unloadModule(name);
		return true;
	}

	protected String getModuleInfo(Object ps)
	{
		String name = (String) ps;
		BPModule m = BPModuleManager.getModule(name);
		return m == null ? "No Module" : m.toString();
	}

	public boolean canHandle(String cmdname)
	{
		String cn = cmdname.toUpperCase();
		switch (cn)
		{
			case CN_INFO:
			case CN_ECHO:
			case CN_LOADMODULE:
			case CN_UNLOADMODULE:
			case CN_MODULEINFO:
				return true;
		}
		return false;
	}

	public String getName()
	{
		return "core";
	}

	public final static BPCommandHandlerList CREATE_MAIN_LIST()
	{
		BPCommandHandlerList rc = new BPCommandHandlerList();
		rc.addHandler(new BPCommandHandlerCore());
		return rc;
	}
}
