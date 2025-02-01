package bp.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface BPClientFactory
{
	String getName();
	
	default List<String> getCategories()
	{
		return new ArrayList<String>();
	}

	BPClient getClient(Map<String, Object> params);
}
