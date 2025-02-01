package bp.processor;

import bp.config.BPConfig;
import bp.config.BPSetting;

public interface BPDataProcessor<T, R>
{
	String getName();

	String getUILabel();

	String getCategory();

	R process(T data, BPConfig config);

	default boolean needSettingUI()
	{
		return false;
	}

	default boolean canInput(String format)
	{
		return false;
	}

	default boolean canOutput(String format)
	{
		return false;
	}

	default R process(T data)
	{
		return process(data, null);
	}

	default BPSetting getSetting(BPConfig preset)
	{
		return null;
	}

	default String getDefaultPart()
	{
		return null;
	}

	default String getResultFactoryClassName()
	{
		return null;
	}
}
