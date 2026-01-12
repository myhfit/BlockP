package bp.service;

import bp.module.BPModule;

public interface BPServiceModuled extends BPService
{
	default boolean isFromModule()
	{
		return true;
	}

	<M extends BPModule> void bindModule(M module);

	<M extends BPModule> M getModule();
}
