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
	public final static String CN_INFO = "CORE_INFO";
	public final static String CN_ECHO = "ECHO";
	public final static String CN_LOADMODULE = "CORE_LOADMODULE";
	public final static String CN_UNLOADMODULE = "CORE_UNLOADMODULE";
	public final static String CN_MODULEINFO = "CORE_MODULEINFO";
	public final static String CN_HELP = "HELP";

	public BPCommandHandlerCore()
	{
		m_cmdnames = ObjUtil.makeList(CN_INFO, CN_ECHO, CN_HELP, CN_LOADMODULE, CN_UNLOADMODULE, CN_MODULEINFO);
	}

	public BPCommandResult call(BPCommand cmd)
	{
		String cmdname = cmd.name.toUpperCase();
		switch (cmdname)
		{
			case CN_INFO:
				return BPCommandResult.OK("BlockP - Core" + getExtensionInfos() + getPlatformInfos());
			case CN_ECHO:
				return BPCommandResult.OK(cmd.ps);
			case CN_HELP:
				return BPCommandResult.RUN(() -> showHelp(cmd.ps));
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

	protected String showHelp(Object ps)
	{
		// String cmdps = (String) ps;
		StringBuilder sb = new StringBuilder();
		List<String> cns = BPCore.getCommandHandler().getCommandNames();
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

	public String getName()
	{
		return "core";
	}

	public final static BPCommandHandlerList CREATE_MAIN_LIST()
	{
		BPCommandHandlerList rc = new BPCommandHandlerList();
		rc.addHandler(new BPCommandHandlerCore());
		rc.addHandler(new BPCommandHandlerTask());
		return rc;
	}
}
