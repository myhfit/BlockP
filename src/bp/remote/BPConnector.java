package bp.remote;

import java.util.function.Function;

import bp.data.BPCommand;
import bp.data.BPCommandResult;

public interface BPConnector
{
	boolean connect();

	void disconnect();

	BPCommandResult call(BPCommand cmd);

	void bindHandler(Function<BPCommand, BPCommandResult> handler);
}
