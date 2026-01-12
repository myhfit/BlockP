package bp.service;

import java.util.List;

public interface BPService
{
	String getName();

	void stop();

	void start();

	void register();

	<T> T call(String action, Object... params);

	default boolean isExportable()
	{
		return false;
	}

	default List<String> getExportList()
	{
		return null;
	}

	default boolean isFromModule()
	{
		return false;
	}
}
