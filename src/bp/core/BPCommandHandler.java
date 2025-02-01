package bp.core;

import bp.data.BPCommand;
import bp.data.BPCommandResult;

public interface BPCommandHandler
{
	BPCommandResult call(BPCommand cmd);

	boolean canHandle(String cmdkey);

	String getName();
}
