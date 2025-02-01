package bp.event;

import java.util.function.Consumer;

public interface BPEventChannel
{
	void on(String key, Consumer<? extends BPEvent> listener);

	void off(String key, Consumer<? extends BPEvent> listener);

	boolean trigger(BPEvent event);
}
