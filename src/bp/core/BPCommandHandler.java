package bp.core;

import java.util.List;

import bp.data.BPCommand;
import bp.data.BPCommandResult;

public interface BPCommandHandler
{
	BPCommandResult call(BPCommand cmd);

	boolean canHandle(String cmdkey);

	String getName();

	List<String> getCommandNames();
}
