package bp.transform;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.util.ObjUtil;

public class BPTransformerFactoryTaskValue implements BPTransformerFactory
{
	public String getName()
	{
		return "Task Value";
	}

	public boolean checkData(Object source)
	{
		if (source == null)
			return false;
		if (source instanceof byte[])
			return true;
		return false;
	}

	public Collection<String> getFunctionTypes()
	{
		return new CopyOnWriteArrayList<String>(new String[] { TF_TOOBJ });
	}

	public BPTransformer<?> createTransformer(String func)
	{
		return new BPTransformerParseTaskValue();
	}

	public static class BPTransformerParseTaskValue extends BPTransformerBase<Object>
	{
		protected Object m_key;

		@SuppressWarnings("unchecked")
		protected Object transform(Object t)
		{
			if (t == null)
				return null;
			if (t instanceof Map)
			{
				return ((Map<String, ?>) t).get(ObjUtil.toString(m_key));
			}
			else if (t instanceof List)
			{
				return ((List<?>) t).get(ObjUtil.toInt(m_key, -1));
			}
			return null;
		}

		public BPSetting getSetting()
		{
			BPSettingBase rc = new BPSettingBase().addItem(BPSettingItem.create("key", "Key", BPSettingItem.ITEM_TYPE_TEXT, null));
			rc.set("key", m_key);
			return rc;
		}

		public void setSetting(BPConfig cfg)
		{
			m_key = (Object) cfg.get("key");
		}

		public String getInfo()
		{
			return "Task Value" + ((m_key == null) ? "[Need Setting]" : ("(" + ObjUtil.toString(m_key) + ")"));
		}
	}
}