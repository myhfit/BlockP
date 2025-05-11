package bp.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BPSettingBase extends BPConfigCommon implements BPSetting
{
	protected List<BPSettingItem> m_items;

	public BPSettingBase()
	{

	}

	public BPSettingBase(BPConfig preset)
	{
		if (preset == null)
			return;
		setAll(preset.getMappedData());
	}

	public BPSettingItem[] getItems()
	{
		List<BPSettingItem> items = m_items;
		if (items == null)
			return null;
		return items.toArray(new BPSettingItem[items.size()]);
	}

	public BPSettingBase addItem(BPSettingItem item)
	{
		if (m_items == null)
			m_items = new ArrayList<BPSettingItem>();
		m_items.add(item);
		return this;
	}

	public void set(String key, Object value)
	{
		if (value == null)
			m_map.remove(key);
		else
			m_map.put(key, value);
	}

	public void setAll(Map<String, Object> kvs)
	{
		if (kvs != null)
		{
			for (String key : kvs.keySet())
			{
				set(key, kvs.get(key));
			}
		}
	}
}
