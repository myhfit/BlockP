package bp.format;

import java.util.function.Consumer;

public interface BPFormatFactory
{
	void register(Consumer<BPFormat> regfunc);
}
