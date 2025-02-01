package bp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import bp.config.BPConfigAdv.BPConfigAdvBase;
import bp.format.BPFormatManager;

public class FormatAssocs extends BPConfigAdvBase
{
	public <S extends BPConfigAdv> Consumer<S> getConfigLoader()
	{
		return null;
	}

	public <S extends BPConfigAdv> void setConfigLoader(Consumer<S> loader)
	{
	}

	public <S extends BPConfigAdv> Consumer<S> getConfigPersister()
	{
		return null;
	}

	public <S extends BPConfigAdv> void setConfigPersister(Consumer<S> persister)
	{
	}

	public Map<String, Object> getMappedData()
	{
		BPFormatManager man = new BPFormatManager();
		return man.getMappedData();
	}

	protected Map<String, Object> createMap()
	{
		return new HashMap<String, Object>();
	}

	public boolean canUserConfig()
	{
		return true;
	}
}