package bp.transform;

import java.util.ArrayList;
import java.util.List;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;

public class BPTransformerSplitText extends BPTransformerBase<String>
{
	protected volatile String m_sp;

	public String getInfo()
	{
		return "Text to List";
	}

	protected Object transform(String t)
	{
		List<String> rc = new ArrayList<String>();
		String sp = m_sp;
		if (sp == null || sp.length() == 0)
			sp = ",";
		String[] strs = t.split(sp);
		for (String str : strs)
			rc.add(str);
		return rc;
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = new BPSettingBase().addItem(BPSettingItem.create("sp", "Separator", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.set("sp", m_sp);
		return rc;
	}

	public void setSetting(BPConfig config)
	{
		m_sp = (String) config.get("sp");
	}
}