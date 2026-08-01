package bp.transform;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.data.BPDataConsumer;
import bp.data.BPDataConsumer.BPDataConsumerBase;
import bp.script.BPScript;
import bp.script.BPScriptBase;
import bp.script.BPScriptManager;
import bp.util.ObjUtil;

public class BPTransformerFactoryCollectScript implements BPTransformerFactory
{
	public String getName()
	{
		return "Collect by Script";
	}

	public boolean checkData(Object source)
	{
		if (source == null)
			return true;
		if (source instanceof List)
			return true;
		return false;
	}

	public Collection<String> getFunctionTypes()
	{
		return new CopyOnWriteArrayList<String>(TF_ALL);
	}

	public BPTransformer<?> createTransformer(String func)
	{
		return new BPTransformerCollectScript();
	}

	public static class BPTransformerCollectScript extends BPDataConsumerBase<Object> implements BPTransformer<Object>
	{
		protected BPDataConsumer<?> m_output;
		protected Object m_data;

		protected BPScript m_script;
		protected String m_scripttext;
		protected BPScriptManager m_man;

		public String getInfo()
		{
			return "Collect by Script";
		}

		public void setOutput(BPDataConsumer<?> pipe)
		{
			m_output = pipe;
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

		public void accept(Object t)
		{
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void finish()
		{
			BPScript script = m_script;
			Map<String, Object> ctx = m_contextref.get();
			Map<String, Object> vars = ObjUtil.makeMap("$in", null, "$ctx", ctx);
			m_data = m_man.runScripts(new BPScript[] { script }, null, null, true, vars);
			BPDataConsumer out = m_output;
			if (out != null)
				out.runSegment(() -> out.accept(m_data));
		}

		public void clear()
		{
			m_script = null;
			m_man.clear();
			m_man = null;
			super.clear();
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