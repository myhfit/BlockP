package bp.context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceIO;
import bp.schedule.BPSchedule;
import bp.script.BPScript;
import bp.script.BPScriptBase;
import bp.script.BPScriptManager;
import bp.task.BPTask;
import bp.task.BPTaskManager;
import bp.util.IOUtil;
import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPWorkspaceContextLocalBase extends BPFileContextLocalBase implements BPWorkspaceContextLocal
{
	protected BPTaskManager m_taskman;
	protected BPTaskManager m_workloadman;

	protected BPScriptManager m_scriptman;

	protected volatile boolean m_hasbpdir;

	protected final static String S_BPDIR = ".bp";

	public BPWorkspaceContextLocalBase(String path)
	{
		super(path);
		m_taskman = new BPTaskManager();
		m_workloadman = new BPTaskManager();
		m_taskman.setManagerFlag(-1);
		m_workloadman.setManagerFlag(-2);
		m_scriptman = new BPScriptManager();
		testBPDir();
	}

	protected void testBPDir()
	{
		BPResourceFileSystem fs = getRootDir().getChild(S_BPDIR, true);
		m_hasbpdir = (fs != null && fs.isDirectory());
	}

	public BPTaskManager getTaskManager()
	{
		return m_taskman;
	}

	public BPTaskManager getWorkLoadManager()
	{
		return m_workloadman;
	}

	public BPScriptManager getScriptManager()
	{
		return m_scriptman;
	}

	public boolean removeTask(BPTask<?> task)
	{
		int mf = task.getManagerFlag();
		if (mf == -1)
		{
			boolean r = getTaskManager().removeTask(task);
			if (r)
				saveTasks();
			return r;
		}
		else if (mf == -2)
			return getWorkLoadManager().removeTask(task);
		return false;
	}

	public BPScript saveScript(BPScript oldsc, Map<String, Object> newsc)
	{
		String oldname = oldsc != null ? oldsc.getName() : null;
		String oldlang = oldsc != null ? oldsc.getLanguage() : null;
		BPScript sc = null;
		String newname = null;
		String newlang = null;
		String newfilename = null;
		if (newsc != null)
		{
			if (oldsc != null)
			{
				sc = oldsc;
			}
			else
			{
				sc = new BPScriptBase();
			}
			sc.setMappedData(newsc);
			newname = sc.getName();
			newlang = sc.getLanguage();
			newfilename = newname + BPScriptManager.getExtByLanguage(newlang);
		}

		BPResourceDir cfgdir = getConfigDir(S_DIR_SCRIPTS, true);
		BPScript rc = sc;
		if (cfgdir != null)
		{
			BPResourceFile fres = null;
			if (oldname != null && oldlang != null && (newname == null || (!newname.equalsIgnoreCase(oldname)) || (!newlang.equals(oldlang))))
			{
				String oldfilename = oldname + BPScriptManager.getExtByLanguage(oldlang);
				BPResourceFileSystem oldfile = cfgdir.getChild(oldfilename, true);
				if (oldfile != null && oldfile.exists())
				{
					oldfile.delete();
				}
				if (newfilename != null)
					fres = (BPResourceFile) cfgdir.createChild(newfilename, true);
			}
			else
			{
				fres = (BPResourceFile) cfgdir.getChild(newfilename, false);
			}
			if (fres != null)
			{
				String stext = sc.getScriptText();
				fres.useOutputStream(out ->
				{
					IOUtil.write(out, TextUtil.fromString(stext, "utf-8"));
					return true;
				});
			}
			if (oldsc == null)
			{
				m_scriptman.addScript(sc);
			}
			else if (newsc == null)
			{
				m_scriptman.removeScript(oldsc);
				rc = oldsc;
			}
		}
		return rc;
	}

	public void loadScripts()
	{
		try
		{
			List<BPScript> scripts = new ArrayList<BPScript>();
			BPResourceDir cfgdir = getConfigDir(S_DIR_SCRIPTS, true);
			if (cfgdir != null)
			{
				BPResourceFileSystem[] chds = cfgdir.list();
				for (BPResourceFileSystem chd : chds)
				{
					if (chd.isFile())
					{
						String ext = chd.getExt();
						String lang = BPScriptManager.getLanguageByExt(ext.toLowerCase());
						if (lang != null)
						{
							BPResourceFile f = (BPResourceFile) chd;
							String sctext = f.useInputStream((in) -> TextUtil.toString(IOUtil.read(in), "utf-8"));
							if (sctext != null && sctext.trim().length() > 0)
							{
								BPScriptBase script = (BPScriptBase) BPScriptManager.createScriptObj(sctext, ext);
								if (script != null)
								{
									String fname = f.getName();
									script.setName(fname.substring(0, fname.lastIndexOf(".")));
									scripts.add(script);
								}
							}
						}
					}
				}
			}
			if (scripts.size() > 0)
			{
				m_scriptman.clear();
				for (BPScript script : scripts)
				{
					m_scriptman.addScript(script);
				}
			}
		}
		catch (Exception e)
		{
			Std.err(e);
		}
	}

	public void saveScripts()
	{
	}

	public Map<String, List<BPSchedule>> loadSchedules()
	{
		final Map<String, List<BPSchedule>> rc = new HashMap<String, List<BPSchedule>>();
		BPResourceIO cfgres = getConfigRes(S_FILENAME_SCHEDULEJ);
		if (cfgres != null)
		{
			Map<String, List<Map<String, Object>>> sdmap = cfgres.useInputStream((in) ->
			{
				Map<String, List<Map<String, Object>>> datas = null;
				try
				{
					byte[] bs = IOUtil.read(in);
					if (bs != null)
					{
						datas = JSONUtil.decode(TextUtil.toString(bs, "utf-8"));
					}
				}
				catch (Exception e)
				{
					Std.err(e);
				}
				return datas;
			});
			if (sdmap != null)
			{
				for (Entry<String, List<Map<String, Object>>> entry : sdmap.entrySet())
				{
					String key = entry.getKey();
					List<BPSchedule> sds = new ArrayList<BPSchedule>();
					List<Map<String, Object>> sdjsons = entry.getValue();
					for (Map<String, Object> sdjson : sdjsons)
					{
						try
						{
							sds.add(ObjUtil.mapToObj(sdjson));
						}
						catch (Exception e)
						{
							Std.err(e);
						}
					}
					rc.put(key, sds);
				}
			}
		}
		return rc;
	}

	public void saveSchedules(Map<String, List<BPSchedule>> schedules)
	{
		Map<String, List<Map<String, Object>>> sdmap = new HashMap<String, List<Map<String, Object>>>();
		for (Entry<String, List<BPSchedule>> entry : schedules.entrySet())
		{
			String key = entry.getKey();
			List<Map<String, Object>> sdlist = new ArrayList<Map<String, Object>>();
			List<BPSchedule> sds = entry.getValue();
			for (BPSchedule sd : sds)
			{
				sdlist.add(sd.getSaveData());
			}
			if (sdlist.size() > 0)
			{
				sdmap.put(key, sdlist);
			}
		}
		BPResourceIO cfgres = getConfigRes(S_FILENAME_SCHEDULEJ, false);
		if (cfgres != null)
		{
			cfgres.useOutputStream((out) ->
			{
				try
				{
					String str = JSONUtil.encode(sdmap);
					IOUtil.write(out, TextUtil.fromString(str, "utf-8"));
					return true;
				}
				catch (Exception e)
				{
					Std.err(e);
				}
				return false;
			});
		}
	}

	public BPResourceDir getBPDir()
	{
		BPResourceDir rootdir = getRootDir();
		if (m_hasbpdir)
		{
			return (BPResourceDir) rootdir.getChild(".bp", true);
		}
		return rootdir;
	}

	public BPResourceIO getConfigRes(String cfgfilename)
	{
		BPResourceDir bpdir = getBPDir();
		BPResource rc = bpdir.getChild(cfgfilename);
		if (rc == null)
			return null;
		if (!rc.isIO())
			return null;
		return (BPResourceIO) rc;
	}

	public BPResourceIO getConfigRes(String cfgfilename, boolean needexist)
	{
		BPResourceDir bpdir = getBPDir();
		BPResource rc = bpdir.getChild(cfgfilename, needexist);
		if (rc == null)
			return null;
		if (!rc.isIO())
			return null;
		return (BPResourceIO) rc;
	}

	public BPResourceDir getConfigDir(String cfgfilename, boolean needexist)
	{
		BPResourceDir bpdir = getRootDir();
		BPResource rc = bpdir.getChild(cfgfilename, needexist);
		if (rc == null)
			return null;
		if (rc.isLeaf())
			return null;
		return (BPResourceDir) rc;
	}

	@SuppressWarnings("unchecked")
	public void loadTasks()
	{
		List<BPTask<?>> tasks = new ArrayList<BPTask<?>>();
		boolean isjson = false;
		BPResourceIO cfgres = getConfigRes(S_FILENAME_TASKS);
		if (cfgres == null)
		{
			cfgres = getConfigRes(S_FILENAME_TASKJ);
			isjson = true;
		}
		if (cfgres != null)
		{
			final boolean fisjson = isjson;
			List<Map<String, Object>> list = cfgres.useInputStream((in) ->
			{
				if (fisjson)
				{
					List<Map<String, Object>> datas = null;
					try
					{
						byte[] bs = IOUtil.read(in);
						if (bs != null)
						{
							datas = JSONUtil.decode(TextUtil.toString(bs, "utf-8"));
						}
					}
					catch (Exception e)
					{
						Std.err(e);
					}
					return datas;
				}
				else
				{
					try (ObjectInputStream ois = new ObjectInputStream(in))
					{
						return (List<Map<String, Object>>) ois.readObject();
					}
					catch (IOException | ClassNotFoundException e)
					{
						Std.err(e);
					}
					return null;
				}
			});
			if (list != null)
			{
				for (Map<String, Object> mo : list)
				{
					try
					{
						tasks.add(ObjUtil.mapToObj(mo));
					}
					catch (Exception e)
					{
						e.printStackTrace();
						Std.err("Task:" + mo.get("name") + " load error");
					}
				}
			}
		}
		for (BPTask<?> task : tasks)
			getTaskManager().addTask(task);
	}

	public void saveTasks()
	{
		List<BPTask<?>> tasks = getTaskManager().listTasks();
		List<Map<String, Object>> taskmos = new ArrayList<Map<String, Object>>();
		for (BPTask<?> task : tasks)
		{
			if (!task.isNoSave())
				taskmos.add(task.getSaveData());
		}
		BPResourceIO cfgres = getConfigRes(S_FILENAME_TASKJ, false);
		if (cfgres != null)
		{
			cfgres.useOutputStream((out) ->
			{
				try
				{
					String str = JSONUtil.encode(taskmos);
					IOUtil.write(out, TextUtil.fromString(str, "utf-8"));
					return true;
				}
				catch (Exception e)
				{
					Std.err(e);
				}
				return false;
			});
		}
	}
}
