package bp.script;

import java.util.Map;

public interface BPScriptContext
{
	Map<String, Object> envs();

	Map<String, Object> vars();

	void bindEnvs(Map<String, Object> newenvs, boolean raw);

	void bindVars(Map<String, Object> newvars);

	void setOutput(Object obj);

	void setInput(Object obj);

	<T> T getOutput();

	<T> T getInput();

	void reverse();
}
