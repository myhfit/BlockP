package bp.schedule;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.BPCore;
import bp.env.BPEnvCommon;
import bp.env.BPEnvManager;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.util.LockUtil;
import bp.util.Std;

public class BPSchedulerFileSystem extends BPSchedulerTimerBase
{
	public final static String NAME_FILESYSTEM = "FileSystem";
	protected volatile WatchService m_service;
	protected Queue<WatchKey> m_wkeys = new ConcurrentLinkedQueue<WatchKey>();
	protected Map<WatchKey, String> m_keymap = new ConcurrentHashMap<WatchKey, String>();

	protected Map<String, List<BPSchedule>> m_ssmap = new ConcurrentHashMap<String, List<BPSchedule>>();

	public BPSchedulerFileSystem()
	{
		m_interval = 1000;
	}

	public String getName()
	{
		return NAME_FILESYSTEM;
	}

	public void install()
	{
		tryCloseService();
		Std.debug("Scheduler:" + getName() + " Installed");
	}

	protected void tryCloseService()
	{
		LockUtil.rwLock(m_slock, true, () ->
		{
			WatchService service = m_service;
			if (service != null)
			{
				try
				{
					service.close();
				}
				catch (IOException e)
				{
					Std.err(e);
				}
				m_service = null;
				stopPool();
			}
		});
	}

	protected WatchService tryCreateService()
	{
		if (!("true".equalsIgnoreCase(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_ENABLE_SCHEDULER_FS))))
			return null;

		return LockUtil.rwLock(m_slock, true, () ->
		{
			WatchService service = m_service;
			if (service == null)
			{
				startPool();
				try
				{
					service = FileSystems.getDefault().newWatchService();
					m_service = service;
				}
				catch (IOException e)
				{
					Std.err(e);
				}
			}
			return service;
		});
	}

	public void uninstall()
	{
		tryCloseService();
		Std.debug("Scheduler:" + getName() + " Unistalled");
	}

	public void addSchedule(BPSchedule s)
	{
		super.addSchedule(s);
		refreshFileList();
	}

	public void removeSchedule(BPSchedule s)
	{
		super.removeSchedule(s);
		refreshFileList();
	}

	protected void refreshFileList()
	{
		List<BPSchedule> ss = new ArrayList<BPSchedule>(m_ss);
		Map<String, List<BPSchedule>> ssmap = new HashMap<String, List<BPSchedule>>();
		for (BPSchedule s : ss)
		{
			BPScheduleFileSystem sfs = (BPScheduleFileSystem) s;
			String f = sfs.getWatchFile();
			if (f != null && f.length() > 0)
			{
				if (f.startsWith("@"))
				{
					f = (new File(f.substring(1))).getAbsolutePath();
				}
				else
				{
					BPResource res = BPCore.getFileContext().getRes(f);
					if (res == null)
						continue;
					f = ((BPResourceFileSystem) res).getFileFullName();
				}
				List<BPSchedule> slist = ssmap.get(f);
				if (slist == null)
				{
					slist = new CopyOnWriteArrayList<BPSchedule>();
					ssmap.put(f, slist);
				}
				slist.add(sfs);
			}
		}
		m_ssmap.clear();
		m_ssmap.putAll(ssmap);

		if (m_ssmap.size() > 0)
		{
			WatchService service = tryCreateService();
			if (service != null)
			{
				Queue<WatchKey> wkeys = m_wkeys;
				for (WatchKey k : wkeys)
				{
					k.cancel();
				}
				wkeys.clear();
				m_keymap.clear();
				List<String> fs = new ArrayList<String>(ssmap.keySet());
				try
				{
					for (String f : fs)
					{
						Path p = Paths.get(f);
						WatchKey wk = p.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
						m_wkeys.add(wk);
						m_keymap.put(wk, f);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			tryCloseService();
		}
	}

	protected void runScheduleInner()
	{
		WatchService service = LockUtil.rwLock(m_slock, false, () -> m_service);
		if (service == null)
			return;
		WatchKey key = service.poll();
		while (key != null)
		{
			for (WatchEvent<?> e : key.pollEvents())
			{
				Path p = (Path) e.context();
				String subname = p.toFile().getName();
				String f = m_keymap.get(key);
				String filename = f + (f.endsWith(File.separator) ? "" : File.separator) + subname;
				if (f != null)
				{
					List<BPSchedule> ss = m_ssmap.get(f);
					if (ss != null)
					{
						for (BPSchedule s : ss)
						{
							s.check(this, f, filename, e.kind().name());
						}
					}
				}
			}
			key.reset();
			key = service.poll();
		}
	}
}
