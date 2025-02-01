package bp.script;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BPScriptContextBase implements BPScriptContext
{
	protected Map<String, Object> m_envs;
	protected Map<String, Object> m_vars;

	protected Object m_output;
	protected Object m_input;

	public BPScriptContextBase(Map<String, Object> _envs, Map<String, Object> _vars)
	{
		m_envs = Collections.unmodifiableMap(_envs == null ? new HashMap<String, Object>() : _envs);
		m_vars = _vars == null ? new HashMap<String, Object>() : _vars;
	}

	public Map<String, Object> vars()
	{
		return m_vars;
	}

	public Map<String, Object> envs()
	{
		return m_envs;
	}

	public void setOutput(Object obj)
	{
		m_output = obj;
	}

	public void setInput(Object obj)
	{
		m_input = obj;
	}

	@SuppressWarnings("unchecked")
	public <T> T getInput()
	{
		return (T) m_input;
	}

	@SuppressWarnings("unchecked")
	public <T> T getOutput()
	{
		return (T) m_output;
	}

	public void reverse()
	{
		m_input = m_output;
		m_output = null;
	}

	public void bindVars(Map<String, Object> newvars)
	{
		m_vars = newvars;
	}

	public void bindEnvs(Map<String, Object> newenvs, boolean raw)
	{
		if (raw)
			m_envs = newenvs;
		else
			m_envs = Collections.unmodifiableMap(newenvs == null ? new HashMap<String, Object>() : newenvs);
	}
}