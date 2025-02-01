package bp.transform;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.data.BPDataConsumer;

public class BPTransformerFactoryCollect2Text implements BPTransformerFactory
{
	public String getName()
	{
		return "Collect to Text";
	}

	public boolean checkData(Object source)
	{
		if (source == null)
			return false;
		if (source instanceof List)
			return true;
		return false;
	}

	public Collection<String> getFunctionTypes()
	{
		return new CopyOnWriteArrayList<String>(new String[] { TF_TOSTRING });
	}

	public BPTransformer<?> createTransformer(String func)
	{
		return new BPTransformerCollect2Text();
	}

	public static class BPTransformerCollect2Text extends BPDataConsumer.BPDataConsumerTextCollector implements BPTransformer<String>
	{
		protected BPDataConsumer<?> m_output;

		public String getInfo()
		{
			return "Collect to Text";
		}

		public void setOutput(BPDataConsumer<?> pipe)
		{
			m_output = pipe;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void finish()
		{
			BPDataConsumer out = m_output;
			if (out != null)
			{
				out.runSegment(() -> out.accept(m_sb.toString()));
			}
		}

		public BPSetting getSetting()
		{
			BPSettingBase rc = new BPSettingBase().addItem(BPSettingItem.create("sp", "Separator", BPSettingItem.ITEM_TYPE_TEXT, null));
			rc.set("sp", m_sp);
			return rc;
		}

		public void setSetting(BPConfig cfg)
		{
			String sp = (String) cfg.get("sp");
			if (sp != null && sp.length() == 0)
				sp = null;
			m_sp = sp;
		}
	}
}