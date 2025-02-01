package bp.schedule;

import java.util.Map;

import bp.BPCore;
import bp.script.BPScript;
import bp.script.BPScriptManager;
import bp.util.ObjUtil;

public class BPScheduleTargetRunScript implements BPScheduleTarget
{
	protected volatile String m_script;
	protected volatile String m_scripttype;

	public void setup(String script, String scripttype)
	{
		m_script = script;
		m_scripttype = scripttype;
	}

	public void accept(Long t, BPScheduleTargetParams params)
	{
		BPScript script = BPScriptManager.createScriptObj(m_script, BPScriptManager.getExtByLanguage(m_scripttype));
		BPCore.getWorkspaceContext().getScriptManager().runScripts(new BPScript[] { script }, ObjUtil.makeMap("time", t, "scheduler", (params == null ? null : params.scheduler)), null, true);
	}

	public final static class BPScheduleTargetFactoryRunScript implements BPScheduleTargetFactory
	{
		public String getName()
		{
			return "Run Script";
		}

		public BPScheduleTarget create(Map<String, Object> params)
		{
			BPScheduleTargetRunScript target = new BPScheduleTargetRunScript();
			target.setup((String) params.get("script"), (String) params.get("scripttype"));
			return target;
		}
	}
}
