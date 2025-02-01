package bp.config;

public interface BPSetting extends BPConfig
{
	BPSettingItem[] getItems();

	void set(String key, Object value);
}
