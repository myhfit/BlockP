package bp.remote;

import java.util.Map;

public interface BPConnectorFactory
{
	String getName();

	BPConnector create(Map<String, Object> ps);
}
