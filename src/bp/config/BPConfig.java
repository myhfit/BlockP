package bp.config;

import bp.data.BPMData;

public interface BPConfig extends BPMData
{
	<V> V get(String k);

	default void load()
	{
	}

	default void save()
	{
	}

	default boolean canUserConfig()
	{
		return false;
	}

	default String getConfigName()
	{
		return this.getClass().getSimpleName();
	}
}
