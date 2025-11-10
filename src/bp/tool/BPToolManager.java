package bp.tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import bp.BPCore;
import bp.BPCore.BPPlatform;
import bp.config.BPConfigAdv;
import bp.config.BPConfigAdv.BPConfigAdvBase;
import bp.util.ClassUtil;
import bp.util.Std;

public class BPToolManager extends BPConfigAdvBase
{
	protected Consumer<? extends BPConfigAdv> m_loader = this::loadConfig;
	protected Consumer<? extends BPConfigAdv> m_persister = this::saveConfig;

	protected final static List<BPToolFactory> s_facs = new CopyOnWriteArrayList<BPToolFactory>();

	@SuppressWarnings("unchecked")
	public <S extends BPConfigAdv> Consumer<S> getConfigLoader()
	{
		return (Consumer<S>) m_loader;
	}

	public <S extends BPConfigAdv> void setConfigLoader(Consumer<S> loader)
	{
		m_loader = loader;
	}

	@SuppressWarnings("unchecked")
	public <S extends BPConfigAdv> Consumer<S> getConfigPersister()
	{
		return (Consumer<S>) m_persister;
	}

	public <S extends BPConfigAdv> void setConfigPersister(Consumer<S> persister)
	{
		m_persister = persister;
	}

	protected void loadConfig(BPConfigAdv config)
	{
		ServiceLoader<BPToolFactory> facs = ClassUtil.getExtensionServices(BPToolFactory.class);
		BPPlatform platform = BPCore.getPlatform();
		List<BPToolFactory> rfacs = new ArrayList<BPToolFactory>();
		s_facs.clear();
		Iterator<BPToolFactory> facit = facs.iterator();
		while (facit.hasNext())
		{
			try
			{
				BPToolFactory fac = facit.next();
				if (fac.canRunAt(platform))
				{
					rfacs.add(fac);
				}
			}
			catch (Error e)
			{
				Std.err(e.toString());
			}
		}
		s_facs.addAll(rfacs);
	}

	protected void saveConfig(BPConfigAdv config)
	{

	}

	protected Map<String, Object> createMap()
	{
		return null;
	}

	public final static List<BPToolFactory> getFactories()
	{
		return new ArrayList<BPToolFactory>(s_facs);
	}

	public final static BPToolFactory getFactory(String name)
	{
		for (BPToolFactory fac : s_facs)
		{
			if (name.equals(fac.getName()))
				return fac;
		}
		return null;
	}
}
