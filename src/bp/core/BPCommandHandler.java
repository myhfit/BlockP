package bp.core;

import java.util.ArrayList;
import java.util.List;

import bp.data.BPCommand;
import bp.data.BPCommandResult;

public interface BPCommandHandler
{
	public final static String CN_EXIT = "exit";
	public final static String CN_HELP = "help";
	
	BPCommandResult call(BPCommand cmd);

	boolean canHandle(String cmdkey);

	String getName();

	List<String> getCommandNames();

	default List<String> getCommandInfos()
	{
		List<String> names = getCommandNames();
		List<String> rc = new ArrayList<String>();
		for (String name : names)
			rc.add("[" + getName() + "]" + name);
		return rc;
	}
}
