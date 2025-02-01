package bp.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BPConfigManager
{
	private final Queue<BPConfig> m_configs = new ConcurrentLinkedQueue<BPConfig>();

	public final void registerConfig(BPConfig config)
	{
		m_configs.add(config);
	}

	public void loadConfigs()
	{
		for (BPConfig config : m_configs)
		{
			config.load();
		}
	}

	public void saveConfigs()
	{
		for (BPConfig config : m_configs)
		{
			config.save();
		}
	}

	public List<BPConfig> getConfigs()
	{
		return new ArrayList<BPConfig>(m_configs);
	}

	public static void clear()
	{

	}
}
