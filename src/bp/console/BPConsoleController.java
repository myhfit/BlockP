package bp.console;

import java.util.function.BiConsumer;

public interface BPConsoleController
{
	boolean writeString(char[] chs);

	boolean writeChar(char ch);

	public void setup(BiConsumer<byte[], Integer> incb, BiConsumer<byte[], Integer> errcb);
}
