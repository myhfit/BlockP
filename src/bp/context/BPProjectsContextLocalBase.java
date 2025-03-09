package bp.context;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.BPCore;
import bp.event.BPEventCoreUI;
import bp.project.BPResourceProject;
import bp.project.BPResourceProjectMemory;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFile;
import bp.res.BPResourceIO;
import bp.schedule.BPSchedule;
import bp.script.BPScript;
import bp.script.BPScriptManager;
import bp.task.BPTask;
import bp.task.BPTaskManager;
import bp.util.IOUtil;
import bp.util.ObjUtil;
import bp.util.ProjectUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPProjectsContextLocalBase implements BPProjectsContext, BPWorkspaceContextLocal
{
	protected BPWorkspaceContextLocal m_fc;
	protected List<BPResourceProject> m_prjs;
	protected volatile BPResourceProject m_tempprj;
	protected final Object m_templock = new Object();

	public final static String S_FILENAME_PRJS = ".bpprjs";

	public BPProjectsContextLocalBase(BPWorkspaceContextLocal fc)
	{
		m_fc = fc;
		createPrjs();
	}

	protected void createPrjs()
	{
		List<BPResourceProject> prjs = new CopyOnWriteArrayList<BPResourceProject>();
		BPResourceIO cfgres = m_fc.getConfigRes(S_FILENAME_PRJS);
		if (cfgres != null)
		{
			Map<String, String> prjsmap = cfgres.useInputStream((in) ->
			{
				try
				{
					String text = new String(IOUtil.read(in), "utf-8");
					return TextUtil.getPlainMap(text);
				}
				catch (UnsupportedEncodingException e)
				{
					Std.err(e);
				}
				return new HashMap<String, String>();
			});
			for (Entry<String, String> entry : prjsmap.entrySet())
			{
				String prjname = entry.getKey();
				String prjpath = entry.getValue();
				BPResourceDir dir = m_fc.getDir(prjpath);
				if (dir.exists())
				{
					BPResourceProject newprj = ProjectUtil.createProjectFromDir(dir, prjname, prjpath);
					if (newprj != null)
						prjs.add(newprj);
				}
			}
		}
		m_prjs = prjs;
	}

	public void initProjects()
	{
		List<BPResourceProject> prjs = new ArrayList<BPResourceProject>(m_prjs);
		for (BPResourceProject prj : prjs)
		{
			prj.initProjectDatas();
		}
	}
	
	public void clearProjects()
	{
		List<BPResourceProject> prjs = new ArrayList<BPResourceProject>(m_prjs);
		for (BPResourceProject prj : prjs)
		{
			prj.clearProjectDatas();
		}
	}

	public BPResource getRes(String filename)
	{
		return m_fc.getRes(filename);
	}

	public boolean isLocal()
	{
		return m_fc.isLocal();
	}

	public void saveProjects()
	{
		BPResourceIO cfgres = m_fc.getConfigRes(S_FILENAME_PRJS, false);
		List<BPResourceProject> prjs = new ArrayList<BPResourceProject>(m_prjs);
		List<BPResourceProject> realprjs = new ArrayList<BPResourceProject>();
		for (BPResourceProject prj : prjs)
		{
			if (!prj.isTemp())
			{
				realprjs.add(prj);
			}
		}
		IOUtil.write(cfgres, TextUtil.fromString(TextUtil.fromPlainMap(ObjUtil.toPlainMap(realprjs, (prj) -> new String[] { prj.getName(), prj.getPath() }), null), "utf-8"));
	}

	public void addProject(BPResourceProject project)
	{
		if (!m_prjs.contains(project))
			m_prjs.add(project);
	}

	public void removeProject(BPResourceProject project)
	{
		if (m_prjs.contains(project))
			m_prjs.remove(project);
	}

	public BPResourceDir getRootDir()
	{
		return m_fc.getRootDir();
	}

	public BPResourceProject[] listProject()
	{
		List<BPResourceProject> rc = new ArrayList<BPResourceProject>(m_prjs);
		BPResourceProject tempprj = m_tempprj;
		if (tempprj != null)
			rc.add(tempprj);
		return rc.toArray(new BPResourceProject[m_prjs.size()]);
	}

	public String getBasePath()
	{
		return m_fc.getBasePath();
	}

	public List<BPResourceFile> findRes(String filename, int limit)
	{
		return m_fc.findRes(filename, limit);
	}

	public boolean isProjectsContext()
	{
		return true;
	}

	public BPResourceDir getDir(String filename)
	{
		return m_fc.getDir(filename);
	}

	public String comparePath(String filename)
	{
		return m_fc.comparePath(filename);
	}

	public BPTaskManager getTaskManager()
	{
		return m_fc.getTaskManager();
	}

	public BPTaskManager getWorkLoadManager()
	{
		return m_fc.getWorkLoadManager();
	}

	public BPScriptManager getScriptManager()
	{
		return m_fc.getScriptManager();
	}

	public void saveTasks()
	{
		m_fc.saveTasks();
	}

	public void loadTasks()
	{
		m_fc.loadTasks();
	}

	public Map<String, List<BPSchedule>> loadSchedules()
	{
		return m_fc.loadSchedules();
	}

	public void saveSchedules(Map<String, List<BPSchedule>> schedules)
	{
		m_fc.saveSchedules(schedules);
	}

	public void saveScripts()
	{
		m_fc.saveScripts();
	}

	public void loadScripts()
	{
		m_fc.loadScripts();
	}

	public boolean checkProjectName(String prjname)
	{
		boolean contains = false;
		if (prjname != null)
		{
			List<BPResourceProject> prjs = new ArrayList<BPResourceProject>(m_prjs);
			for (BPResourceProject prj : prjs)
			{
				if (prj.getName().equals(prjname))
				{
					contains = true;
					break;
				}
			}
		}
		return !contains;
	}

	public BPResourceProject getProjectByName(String name)
	{
		List<BPResourceProject> prjs = new ArrayList<BPResourceProject>(m_prjs);
		for (BPResourceProject prj : prjs)
		{
			if (prj.getName().equals(name))
			{
				return prj;
			}
		}
		return null;
	}

	public BPResourceProject getProject(String key)
	{
		List<BPResourceProject> prjs = new ArrayList<BPResourceProject>(m_prjs);
		for (BPResourceProject prj : prjs)
		{
			if (key.equals(prj.getProjectKey()))
			{
				return prj;
			}
		}
		return null;
	}

	public BPScript saveScript(BPScript oldsc, Map<String, Object> newsc)
	{
		return m_fc.saveScript(oldsc, newsc);
	}

	public BPResourceProject getRootProject(BPResource res)
	{
		List<BPResourceProject> prjs = new ArrayList<BPResourceProject>(m_prjs);
		for (BPResourceProject prj : prjs)
		{
			if (prj.containResource(res))
				return prj;
		}
		BPResourceProject tempprj = m_tempprj;
		if (tempprj != null && tempprj.containResource(res))
			return tempprj;
		return null;
	}

	public BPResourceProject getOrCreateTempProject()
	{
		if (m_tempprj == null)
		{
			boolean f = false;
			synchronized (m_templock)
			{
				if (m_tempprj == null)
				{
					m_tempprj = new BPResourceProjectMemory();
					m_tempprj.setName("Temp");
					f = true;
				}
			}
			if (f)
			{
				BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.refreshProjectTree(null));
			}
		}
		return m_tempprj;
	}

	public void sendProjectChangedEvent()
	{
		BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.refreshProjectTree(null));
	}

	public BPResourceIO getConfigRes(String cfgfilename, boolean needexist)
	{
		return m_fc.getConfigRes(cfgfilename, needexist);
	}

	public BPResourceIO getConfigRes(String cfgfilename)
	{
		return m_fc.getConfigRes(cfgfilename);
	}

	public BPResourceDir getConfigDir(String cfgfilename, boolean needexist)
	{
		return m_fc.getConfigDir(cfgfilename, needexist);
	}

	public boolean removeTask(BPTask<?> task)
	{
		return m_fc.removeTask(task);
	}
}
