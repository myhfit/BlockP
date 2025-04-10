package bp;

import static bp.util.LockUtil.rwLock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bp.cache.BPCacheFileSystemBase;
import bp.config.BPConfig;
import bp.config.BPConfigManager;
import bp.context.BPContext;
import bp.context.BPFileContext;
import bp.context.BPProjectsContextLocalBase;
import bp.context.BPWorkspaceContextLocal;
import bp.context.BPWorkspaceContextLocalBase;
import bp.core.BPCommandHandler;
import bp.core.BPCommandHandlerCore;
import bp.core.BPCommandHandlerList;
import bp.data.BPCommand;
import bp.data.BPCommandResult;
import bp.context.BPProjectsContext;
import bp.context.BPWorkspaceContext;
import bp.env.BPEnvCommon;
import bp.env.BPEnvManager;
import bp.event.BPEventBus;
import bp.event.BPEventChannelBase;
import bp.ext.BPExtensionLoader;
import bp.ext.BPExtensionManager;
import bp.format.BPFormatManager;
import bp.id.IDGenerator;
import bp.id.SerialIDGenerator;
import bp.schedule.BPSchedule;
import bp.schedule.BPScheduleGC;
import bp.schedule.BPScheduler;
import bp.schedule.BPSchedulerCommon;
import bp.schedule.BPSchedulerFileSystem;
import bp.service.BPServiceManager;
import bp.task.BPTask;
import bp.util.CommandLineArgs;
import bp.util.Std;

public class BPCore
{
	protected final static Map<String, String> S_CMDPARAMS = new HashMap<String, String>();
	protected final static ReadWriteLock S_CORELOCK = new ReentrantReadWriteLock();
	public final static BPCacheFileSystemBase FS_CACHE = new BPCacheFileSystemBase();
	protected static volatile BPProjectsContext S_PRJSCONTEXT = null;
	protected final static BPConfigManager s_confman = new BPConfigManager();
	protected static volatile boolean s_autosaveconfig = false;
	protected final static List<BPScheduler> S_SS = new CopyOnWriteArrayList<BPScheduler>();

	protected static IDGenerator S_IDGEN;

	public final static BPEventBus EVENTS_CACHE = new BPEventBus();
	public final static BPEventBus EVENTS_CORE = new BPEventBus();
	public final static BPEventBus EVENTS_EXTENSION = new BPEventBus();

	protected final static BPCommandHandlerList S_CH = BPCommandHandlerCore.CREATE_MAIN_LIST();

	protected static int S_EVENT_CH_FS;
	protected static int S_EVENT_CH_COREUI;

	private static BPPlatform s_platform = BPPlatform.CLI;

	public final static void registerConfig(BPConfig config)
	{
		s_confman.registerConfig(config);
	}

	public final static BPConfigManager getConfigManager()
	{
		return s_confman;
	}

	public final static void start(String contextpath)
	{
		loadExtensions();
		rwLock(S_CORELOCK, true, () ->
		{
			S_IDGEN = new SerialIDGenerator();
			S_EVENT_CH_FS = EVENTS_CACHE.addChannel(new BPEventChannelBase());
			S_EVENT_CH_COREUI = EVENTS_CORE.addChannel(new BPEventChannelBase());
			setLocalFileContext(contextpath == null ? "" : contextpath);
			BPFormatManager.init();
			BPEnvManager.init();
			loadConfigs();
			S_PRJSCONTEXT.initProjects();
			installExtensions();
			if ("true".equalsIgnoreCase(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_ENABLE_SCHEDULE)))
			{
				if (S_SS.size() == 0)
				{
					BPScheduler scommon = new BPSchedulerCommon();
					BPScheduler sfs = new BPSchedulerFileSystem();
					S_SS.add(scommon);
					S_SS.add(sfs);
				}
				for (BPScheduler s : S_SS)
				{
					s.install();
				}
				loadSchedules();
			}
		});
	}

	protected final static void loadExtensions()
	{
		BPExtensionLoader[] loaders = BPExtensionManager.getExtensionLoaders();
		if (loaders != null && loaders.length > 0)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Extensions:");
			boolean flag = false;
			for (BPExtensionLoader loader : loaders)
			{
				loader.preload();
				if (flag)
					sb.append(",");
				else
					flag = true;
				sb.append(loader.getName());
			}
			Std.info(sb.toString());
		}
	}

	protected final static void installExtensions()
	{
		BPExtensionLoader[] loaders = BPExtensionManager.getExtensionLoaders();
		if (loaders != null && loaders.length > 0)
		{
			for (BPExtensionLoader loader : loaders)
			{
				loader.install(S_PRJSCONTEXT);
			}
		}
	}

	protected final static void uninstallExtensions()
	{
		BPExtensionLoader[] loaders = BPExtensionManager.getExtensionLoaders();
		if (loaders != null && loaders.length > 0)
		{
			for (BPExtensionLoader loader : loaders)
			{
				loader.uninstall(S_PRJSCONTEXT);
			}
		}
	}

	public final static void setCommandLineArgs(CommandLineArgs args)
	{
		S_CMDPARAMS.clear();
		S_CMDPARAMS.putAll(args.params);
	}

	public final static void setCommandLineParams(Map<String, String> cmdparams)
	{
	}

	private static void loadConfigs()
	{
		s_confman.loadConfigs();
	}

	public final static void loadSchedules()
	{
		if (!("true".equalsIgnoreCase(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_ENABLE_SCHEDULE))))
			return;
		Map<String, BPScheduler> smap = new HashMap<String, BPScheduler>();
		for (BPScheduler s : S_SS)
		{
			s.removeAll();
			smap.put(s.getName(), s);
		}
		Map<String, List<BPSchedule>> sdmap = S_PRJSCONTEXT.loadSchedules();
		for (Entry<String, List<BPSchedule>> entry : sdmap.entrySet())
		{
			BPScheduler scheduler = smap.get(entry.getKey());
			List<BPSchedule> ss = entry.getValue();
			for (BPSchedule s : ss)
			{
				scheduler.addSchedule(s);
			}
		}
		if ("true".equalsIgnoreCase(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_AUTO_GC)))
		{
			smap.get(BPSchedulerCommon.NAME_COMMON).addSchedule(new BPScheduleGC());
		}
	}

	public final static void saveSchedules()
	{
		if (!("true".equalsIgnoreCase(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_ENABLE_SCHEDULE))))
			return;
		S_PRJSCONTEXT.saveSchedules(getScheduleMap());
	}

	public final static Map<String, List<BPSchedule>> getScheduleMap()
	{
		Map<String, List<BPSchedule>> sdmap = new HashMap<String, List<BPSchedule>>();
		if ("true".equalsIgnoreCase(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_ENABLE_SCHEDULE)))
		{
			for (BPScheduler scheduler : S_SS)
			{
				String key = scheduler.getName();
				List<BPSchedule> sds = scheduler.getSchedules();
				List<BPSchedule> sds2 = new ArrayList<BPSchedule>();
				for (BPSchedule sd : sds)
				{
					if (!sd.isTemp())
						sds2.add(sd);
				}
				sdmap.put(key, sds2);
			}
		}
		return sdmap;
	}

	public static BPCommandResult callCommand(BPCommand cmd)
	{
		return S_CH.call(cmd);
	}

	public static BPCommandHandler getCommandHandler()
	{
		return S_CH;
	}

	private static void saveConfigs()
	{
		s_confman.saveConfigs();
	}

	public final static void setLocalFileContext(String path)
	{
		rwLock(S_CORELOCK, true, () ->
		{
			if (S_PRJSCONTEXT != null)
				S_PRJSCONTEXT.clearProjects();

			BPWorkspaceContextLocal wscontext = new BPWorkspaceContextLocalBase(path);
			BPProjectsContextLocalBase prjscontext = new BPProjectsContextLocalBase(wscontext);
			wscontext.loadTasks();
			wscontext.loadScripts();
			S_PRJSCONTEXT = prjscontext;
		});
	}

	public final static BPFileContext getFileContext()
	{
		return rwLock(S_CORELOCK, false, () -> S_PRJSCONTEXT);
	}

	public final static BPWorkspaceContext getWorkspaceContext()
	{
		return rwLock(S_CORELOCK, false, () -> S_PRJSCONTEXT);
	}

	public final static BPProjectsContext getProjectsContext()
	{
		return rwLock(S_CORELOCK, false, () -> S_PRJSCONTEXT);
	}

	public final static boolean isLocalContext()
	{
		return rwLock(S_CORELOCK, false, () -> S_PRJSCONTEXT.isLocal());
	}

	public final static void stop()
	{
		rwLock(S_CORELOCK, false, () ->
		{
			for (BPScheduler s : S_SS)
			{
				s.uninstall();
			}
			BPServiceManager.stopAll();
			S_PRJSCONTEXT.getTaskManager().stopAll();
			S_PRJSCONTEXT.getWorkLoadManager().stopAll();
			uninstallExtensions();
			if (s_autosaveconfig)
				saveConfigs();
			EVENTS_CACHE.removeChannel(S_EVENT_CH_FS);
			EVENTS_CORE.removeChannel(S_EVENT_CH_COREUI);
		});
	}

	public final static void save()
	{
		rwLock(S_CORELOCK, false, () -> saveConfigs());
	}

	public final static int getFileSystemCacheChannelID()
	{
		return S_EVENT_CH_FS;
	}

	public final static int getCoreUIChannelID()
	{
		return S_EVENT_CH_COREUI;
	}

	public final static BPScheduler getCommonScheduler()
	{
		for (BPScheduler s : S_SS)
		{
			if (BPSchedulerCommon.NAME_COMMON.equals(s.getName()))
			{
				return s;
			}
		}
		return null;
	}

	public final static List<BPScheduler> getSchedulers()
	{
		return new ArrayList<BPScheduler>(S_SS);
	}

	public static String genID(BPContext context)
	{
		return (context != null ? (context.isLocal() ? "file:local:temp" : "file:temp") : "") + S_IDGEN.genID();
	}

	public final static void addTask(BPTask<?> task)
	{
		BPWorkspaceContext context = getWorkspaceContext();
		context.getTaskManager().addTask(task);
		context.saveTasks();
	}

	public final static void removeTask(BPTask<?> task)
	{
		BPWorkspaceContext context = getWorkspaceContext();
		context.removeTask(task);
	}

	public final static List<BPTask<?>> listTasks()
	{
		return getWorkspaceContext().getTaskManager().listTasks();
	}

	public final static void saveTasks()
	{
		getWorkspaceContext().saveTasks();
	}

	public final static void saveScripts()
	{
		getWorkspaceContext().saveScripts();
	}

	public final static void setPlatform(BPPlatform platform)
	{
		s_platform = platform;
	}

	public final static BPPlatform getPlatform()
	{
		return s_platform;
	}

	public static enum BPPlatform
	{
		CLI, GUI_SWING
	}
}