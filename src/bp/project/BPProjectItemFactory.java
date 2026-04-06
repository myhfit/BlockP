package bp.project;

import java.util.Map;

import bp.config.BPSetting;
import bp.res.BPResource;

public interface BPProjectItemFactory
{
	String getName();

	void create(Map<String, Object> params, BPResourceProject project, BPResource par);

	String getItemClassName();

	default BPSetting getSetting()
	{
		return null;
	}
}
