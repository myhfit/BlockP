package bp.tool;

import java.util.function.BiConsumer;

import bp.BPCore.BPPlatform;

public interface BPToolFactory
{
	String getName();

	boolean canRunAt(BPPlatform platform);

	void install(BiConsumer<String,BPTool> installfunc, BPPlatform platform);
}
