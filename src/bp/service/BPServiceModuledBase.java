package bp.service;

import java.lang.ref.WeakReference;

import bp.module.BPModule;

public abstract class BPServiceModuledBase extends BPServiceFreeCall implements BPServiceModuled
{
	protected WeakReference<BPModule> m_moduleref;

	public void bindModule(BPModule module)
	{
		m_moduleref = new WeakReference<BPModule>(module);
	}

	@SuppressWarnings("unchecked")
	public <M extends BPModule> M getModule()
	{
		return (M) m_moduleref.get();
	}
}