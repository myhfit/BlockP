package bp.event;

public abstract class BPEvent
{
	public volatile String key;
	public volatile boolean stopNext = false;
	public volatile boolean stopDefault = false;
	public volatile boolean triggerLater = false;
}
