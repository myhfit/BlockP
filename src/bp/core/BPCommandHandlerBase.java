package bp.core;

import java.util.List;

public abstract class BPCommandHandlerBase implements BPCommandHandler
{
	protected List<String> m_cmdnames;

	public List<String> getCommandNames()
	{
		return m_cmdnames;
	}

	public boolean canHandle(String cmdname)
	{
		List<String> cmdnames = m_cmdnames;
		if (cmdnames == null)
			return false;
		String cn = cmdname.toUpperCase();
		return cmdnames.contains(cn);
	}
}
