package bp.event;

public abstract class BPEvent
{
	public String key;
	public boolean stopNext = false;
	public boolean stopDefault = false;
	public boolean triggerLater = false;
}
