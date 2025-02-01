package bp.script;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import bp.util.LockUtil;
import bp.util.Std;

public class BPScriptRuntime
{
	public BPScriptContext context;
	public ScriptEngine engine;
	private ConcurrentLinkedQueue<BPScript> scripts;

	protected final ReadWriteLock sclock = new ReentrantReadWriteLock();

	public BPScriptRuntime()
	{
		this(null, null);
	}

	public BPScriptRuntime(Map<String, Object> envs, Map<String, Object> vars)
	{
		scripts = new ConcurrentLinkedQueue<BPScript>();
		context = new BPScriptContextBase(envs, vars);
	}

	public void setupRuntime(String lang)
	{
		LockUtil.rwLock(sclock, true, () ->
		{
			if (scripts.size() == 0)
			{
				ScriptEngineManager man = new ScriptEngineManager();
				engine = man.getEngineByName(lang);
				engine.put("$context", context);
			}
		});
	}

	public void loadScript(BPScript script)
	{
		if (LockUtil.rwLock(sclock, true, () ->
		{
			if (scripts.size() == 0)
			{
				ScriptEngineManager man = new ScriptEngineManager();
				engine = man.getEngineByName(script.getLanguage());
				engine.put("$context", context);
			}
			if (scripts.contains(script))
				return false;
			scripts.add(script);
			return true;
		}))
		{
			try
			{
				engine.eval(script.getScriptText());
			}
			catch (ScriptException e)
			{
				Std.err(e);
				throw new RuntimeException(e);
			}
		}
	}

	public Object runScript(String sc)
	{
		return LockUtil.rwLock(sclock, false, () ->
		{
			try
			{
				return engine.eval(sc);
			}
			catch (ScriptException e)
			{
				Std.err(e);
				throw new RuntimeException(e);
			}
		});
	}
}
