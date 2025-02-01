package bp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import bp.config.BPConfigAdv.BPConfigAdvBase;

public class BPConfigSimple extends BPConfigAdvBase
{
	protected volatile Consumer<? extends BPConfigAdv> m_loader;
	protected volatile Consumer<? extends BPConfigAdv> m_persister;

	protected void onLoad(BPConfigAdv config)
	{
	}

	protected void onSave(BPConfigAdv config)
	{
	}

	@SuppressWarnings("unchecked")
	public <S extends BPConfigAdv> Consumer<S> getConfigLoader()
	{
		return m_loader != null ? (Consumer<S>) m_loader : this::onLoad;
	}

	public <S extends BPConfigAdv> void setConfigLoader(Consumer<S> loader)
	{
		m_loader = loader;
	}

	@SuppressWarnings("unchecked")
	public <S extends BPConfigAdv> Consumer<S> getConfigPersister()
	{
		return m_persister != null ? (Consumer<S>) m_persister : this::onSave;
	}

	public <S extends BPConfigAdv> void setConfigPersister(Consumer<S> persister)
	{
		m_persister = persister;
	}

	protected Map<String, Object> createMap()
	{
		return new HashMap<String, Object>();
	}

	public static BPConfig fromData(Map<String, Object> configdata)
	{
		BPConfig rc = new BPConfigSimple();
		rc.setMappedData(configdata);
		return rc;
	}
}
