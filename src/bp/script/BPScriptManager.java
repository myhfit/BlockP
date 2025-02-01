package bp.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import bp.BPCore;
import bp.event.BPEventCoreUI;
import bp.util.Std;

public class BPScriptManager
{
	private final List<BPScript> m_scripts = new CopyOnWriteArrayList<BPScript>();

	public void addScript(BPScript script)
	{
		m_scripts.add(script);
		BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.scriptAdded(script));
	}

	public void removeScript(BPScript script)
	{
		m_scripts.remove(script);
		BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.scriptRemoved(script));
	}

	public List<BPScript> listScripts()
	{
		return new ArrayList<BPScript>(m_scripts);
	}

	public BPScript getScript(String name)
	{
		BPScript rc = null;
		List<BPScript> scripts = listScripts();
		for (BPScript sc : scripts)
		{
			if (sc.getName().equals(name))
			{
				rc = sc;
				break;
			}
		}
		return rc;
	}

	public void clear()
	{
		try
		{
			while (m_scripts.size() > 0)
			{
				BPScript script = m_scripts.remove(0);
				BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.scriptRemoved(script));
			}
		}
		catch (Exception e)
		{
			Std.err(e);
		}
	}

	protected final static void putRawContext(ScriptEngine engine, Map<String, Object> rawcontext)
	{
		if (rawcontext != null)
		{
			for (String key : rawcontext.keySet())
				engine.put(key, rawcontext.get(key));
		}
	}

	public <T> T runScripts(BPScript[] scripts, Map<String, Object> envs, Map<String, Object> vars, boolean separatorengine)
	{
		return runScripts(scripts, envs, vars, separatorengine, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T runScripts(BPScript[] scripts, Map<String, Object> envs, Map<String, Object> vars, boolean separatorengine, Map<String, Object> rawcontext)
	{
		T result = null;
		BPScriptContext context = new BPScriptContextBase(envs, null);
		if (vars != null)
			context.vars().putAll(vars);
		ScriptEngine engine = null;
		ScriptEngineManager man = new ScriptEngineManager();
		if (scripts != null)
		{
			if (!separatorengine && scripts.length > 0)
			{
				engine = man.getEngineByName(scripts[0].getLanguage());
				engine.put("_context", context);
				putRawContext(engine, rawcontext);
			}
			for (BPScript script : scripts)
			{
				if (separatorengine)
				{
					engine = (new ScriptEngineManager()).getEngineByName(script.getLanguage());
					engine.put("_context", context);
					putRawContext(engine, rawcontext);
				}
				try
				{
					result = (T) engine.eval(script.getScriptText());
				}
				catch (Exception e)
				{
					Std.err(e);
					throw new RuntimeException(e);
				}
			}
		}
		return result;
	}

	public final static List<String> getLanguages()
	{
		List<String> rc = new ArrayList<String>();
		try
		{
			ScriptEngineManager man = new ScriptEngineManager();
			List<ScriptEngineFactory> facs = man.getEngineFactories();
			for (ScriptEngineFactory fac : facs)
			{
				rc.add(fac.getLanguageName());
			}
		}
		catch (Throwable e)
		{
			Std.err(new RuntimeException(e));
		}
		return rc;
	}

	public final static String getLanguageByExt(String ext)
	{
		switch (ext)
		{
			case ".js":
			{
				return "ECMAScript";
			}
			case ".groovy":
			{
				return "Groovy";
			}
		}
		return null;
	}

	public final static String getExtByLanguage(String language)
	{
		switch (language)
		{
			case "ECMAScript":
			{
				return ".js";
			}
			case "Groovy":
			{
				return ".groovy";
			}
		}
		return null;
	}

	public final static BPScript createScriptObj(String text, String ext)
	{
		BPScriptBase script = null;
		String lang = getLanguageByExt(ext);
		if (lang != null)
		{
			script = new BPScriptBase();
			script.setScriptText(text);
			script.setLanguage(lang);
		}
		return script;
	}
}
