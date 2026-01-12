package bp.env;

import java.util.List;

import bp.data.BPMData;

public interface BPEnv extends BPMData
{
	List<String> listKeys();

	List<String> listRawKeys();

	boolean isRawKey(String key);

	void setEnv(String key, String value);
	
	void clearEnv(String key);

	String getName();

	String getValue(String key);

	default boolean customKey()
	{
		return false;
	}

	BPEnv getSub(String prefix);
}
