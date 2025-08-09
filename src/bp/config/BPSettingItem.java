package bp.config;

public class BPSettingItem
{
	public String key;
	public String name;
	public String itemtype;
	public String[] candidates;
	public boolean required;

	public final static String ITEM_TYPE_TEXT = "TEXT";
	public final static String ITEM_TYPE_INTEGER = "INT";
	public final static String ITEM_TYPE_FLOAT = "FLOAT";
	public final static String ITEM_TYPE_SELECT = "SELECT";
	public final static String ITEM_TYPE_RESOURCE = "RESOURCE";
	public final static String ITEM_TYPE_RESOURCE_SAVE = "RESOURCE_SAVE";

	public BPSettingItem setRequired(boolean flag)
	{
		required = flag;
		return this;
	}

	public final static BPSettingItem create(String key, String name, String itemtype, String[] candidates)
	{
		BPSettingItem rc = new BPSettingItem();
		rc.key = key;
		rc.name = name;
		rc.itemtype = itemtype;
		rc.candidates = candidates;
		return rc;
	}
}
