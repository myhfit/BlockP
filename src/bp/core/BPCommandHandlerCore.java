package bp.core;

import java.util.List;

import bp.BPCore;
import bp.data.BPCommand;
import bp.data.BPCommandResult;
import bp.ext.BPExtensionLoader;
import bp.ext.BPExtensionManager;
import bp.module.BPModule;
import bp.module.BPModuleManager;
import bp.util.FileUtil;
import bp.util.ObjUtil;

public class BPCommandHandlerCore extends BPCommandHandlerBase implements BPCommandHandler
{
	public final static String CN_INFO = "core_info";
	public final static String CN_ECHO = "echo";
	public final static String CN_GC = "gc";
	public final static String CN_LOADMODULE = "core_loadmodule";
	public final static String CN_UNLOADMODULE = "core_unloadmodule";
	public final static String CN_MODULEINFO = "core_moduleinfo";
	public final static String CN_CMDNAME_LIST = "cmdname_list";

	public BPCommandHandlerCore()
	{
		m_cmdnames = ObjUtil.makeList(CN_INFO, CN_ECHO, CN_HELP, CN_EXIT, CN_GC, CN_LOADMODULE, CN_UNLOADMODULE, CN_MODULEINFO, CN_CMDNAME_LIST);
	}

	public BPCommandResult call(BPCommand cmd)
	{
		String cmdname = cmd.name.toLowerCase();
		switch (cmdname)
		{
			case CN_INFO:
				return BPCommandResult.OK("BlockP - Core" + getExtensionInfos() + getPlatformInfos());
			case CN_ECHO:
				return BPCommandResult.OK(cmd.ps);
			case CN_GC:
				return BPCommandResult.OK(gc());
			case CN_HELP:
				return BPCommandResult.RUN(() -> showHelp(cmd.ps));
			case CN_EXIT:
				return BPCommandResult.RUN_B(() -> tryExit(cmd.ps));
			case CN_CMDNAME_LIST:
				return BPCommandResult.RUN(() -> BPCore.getCommandHandler().getCommandNames());
			case CN_LOADMODULE:
				return BPCommandResult.RUN_B(() -> loadModule(cmd.ps));
			case CN_UNLOADMODULE:
				return BPCommandResult.RUN_B(() -> unloadModule(cmd.ps));
			case CN_MODULEINFO:
				return BPCommandResult.RUN(() -> getModuleInfo(cmd.ps));
		}
		return null;
	}

	protected String getExtensionInfos()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n  Extensions:");
		BPExtensionLoader[] exts = BPExtensionManager.getExtensionLoaders();
		for (BPExtensionLoader ext : exts)
		{
			sb.append("\n    " + ext.getName());
		}
		return sb.toString();
	}

	protected String getPlatformInfos()
	{
		return "\n  Platform:" + BPCore.getPlatform().name();
	}

	protected boolean gc()
	{
		System.gc();
		return true;
	}

	protected String showHelp(Object ps)
	{
		StringBuilder sb = new StringBuilder();
		List<String> cns = BPCore.getCommandHandler().getCommandInfos();
		if (cns != null)
		{
			for (String cn : cns)
			{
				if (sb.length() > 0)
					sb.append("\n");
				sb.append(cn);
			}
		}
		return sb.toString();
	}

	protected boolean tryExit(Object ps)
	{
		BPCore.safeExit();
		return true;
	}

	protected boolean loadModule(Object ps)
	{
		String filename = (String) ps;
		String cfilename = FileUtil.getContextFileFullName(filename);
		if (cfilename != null)
		{
			List<?> rc = BPModuleManager.loadModules(cfilename);
			return rc != null && rc.size() > 0;
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
		if (name == null)
			name = "";
		BPModule m = BPModuleManager.getModule(name);
		return m == null ? ("No Module:" + name) : m.toString();
	}

	public String getName()
	{
		return "Core";
	}

	public final static BPCommandHandlerList CREATE_MAIN_LIST()
	{
		BPCommandHandlerList rc = new BPCommandHandlerList();
		rc.addHandler(new BPCommandHandlerCore());
		rc.addHandler(new BPCommandHandlerTask());
		rc.addHandler(new BPCommandHandlerClient());
		return rc;
	}
}
