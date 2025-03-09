package bp.transform;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.script.BPScript;
import bp.script.BPScriptBase;
import bp.script.BPScriptManager;
import bp.util.ObjUtil;

public class BPTransformerFactoryScript implements BPTransformerFactory
{
	public String getName()
	{
		return "Script";
	}

	public boolean checkData(Object source)
	{
		return true;
	}

	public Collection<String> getFunctionTypes()
	{
		return new CopyOnWriteArrayList<String>(TF_ALL);
	}

	public BPTransformer<?> createTransformer(String func)
	{
		return new BPTransformerCompute();
	}

	protected static class BPTransformerCompute extends BPTransformerBase<Object>
	{
		protected BPScript m_script;
		protected String m_scripttext;
		protected BPScriptManager m_man;

		public String getInfo()
		{
			return "Script";
		}

		public void setup()
		{
			super.setup();
			m_script = null;
			String script = m_scripttext;
			if (script != null)
			{
				m_script = new BPScriptBase();
				m_script.setMappedData(ObjUtil.makeMap("scripttext", script, "language", "javascript"));
			}
			m_man = new BPScriptManager();
		}

		public void clear()
		{
			m_script = null;
			m_man.clear();
			m_man = null;
			super.clear();
		}

		protected Object transform(Object t)
		{
			BPScript script = m_script;
			Map<String, Object> vars = ObjUtil.makeMap("$in", t);
			return m_man.runScripts(new BPScript[] { script }, null, null, true, vars);
		}

		public BPSetting getSetting()
		{
			BPSettingBase rc = new BPSettingBase().addItem(BPSettingItem.create("script", "Script", BPSettingItem.ITEM_TYPE_TEXT, null));
			rc.set("script", m_scripttext);
			return rc;
		}

		public void setSetting(BPConfig cfg)
		{
			String script = (String) cfg.get("script");
			if (script != null && script.length() == 0)
				script = null;
			m_scripttext = script;
		}
	}
}
