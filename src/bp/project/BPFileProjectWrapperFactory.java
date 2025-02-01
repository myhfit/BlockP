package bp.project;

import java.util.function.BiConsumer;
import java.util.function.Function;

import bp.res.BPResource;

public interface BPFileProjectWrapperFactory
{
	void installWrapper(BiConsumer<String, Function<BPResource, BPResource>> cb);
}
