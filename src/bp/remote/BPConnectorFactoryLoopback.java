package bp.remote;

import java.util.Map;

public class BPConnectorFactoryLoopback implements BPConnectorFactory
{
	public String getName()
	{
		return "loopback";
	}

	public BPConnector create(Map<String, Object> ps)
	{
		return null;
	}
}
