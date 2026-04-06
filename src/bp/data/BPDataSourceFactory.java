package bp.data;

import java.util.Map;

import bp.config.BPSetting;

public interface BPDataSourceFactory
{
	String getName();

	default BPDataSource create()
	{
		return create(null);
	}

	BPDataSource create(Map<String, Object> dsdata);

	String getProjectItemClassName();

	default BPSetting getSetting()
	{
		return null;
	}
}
