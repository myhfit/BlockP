package bp.transform;

import java.util.Date;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.util.DateUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPTransformerToString extends BPTransformerBase<Object>
{
	protected volatile String m_en = null;

	protected Object transform(Object t)
	{
		String en = m_en;
		if (en != null && en.length() == 0)
			en = null;
		if (t == null)
			return null;
		if (t instanceof byte[])
		{
			return TextUtil.toString((byte[]) t, ((en == null) ? "utf-8" : en));
		}
		else if (t instanceof Number)
		{
			return ObjUtil.toString(t);
		}
		else if (t instanceof String)
		{
			return t;
		}
		else if (t instanceof Date)
		{
			return DateUtil.formatTime(((Date) t).getTime());
		}
		else if (t.getClass().isPrimitive())
		{
			return ObjUtil.toString(t);
		}
		else
		{
			return ObjUtil.toString(t);
		}
	}

	public String getInfo()
	{
		return "Common to Text";
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = new BPSettingBase().addItem(BPSettingItem.create("encoding", "Encoding", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.set("encoding", m_en);
		return rc;
	}

	public void setSetting(BPConfig cfg)
	{
		if (cfg != null)
		{
			m_en = TextUtil.eds(cfg.get("encoding"));
		}
	}
}
