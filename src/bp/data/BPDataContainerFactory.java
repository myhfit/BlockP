package bp.data;

import bp.config.BPConfig;
import bp.config.BPSetting;

public interface BPDataContainerFactory
{
	BPSetting getSetting();

	boolean canHandle(String format);

	String getName();

	<T extends BPDataContainer> T createContainer(BPConfig config);

	String getFormat();
}
