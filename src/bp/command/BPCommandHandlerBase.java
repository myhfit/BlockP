package bp.command;

import java.util.ArrayList;
import java.util.List;

import bp.util.ObjUtil;
import bp.util.ProcessUtil;

public abstract class BPCommandHandlerBase implements BPCommandHandler
{
	protected List<String> m_cmdnames;

	public List<String> getCommandNames()
	{
		return new ArrayList<String>(m_cmdnames);
	}

	public List<String> getCommandInfos()
	{
		List<String> names = new ArrayList<String>(m_cmdnames);
		List<String> rc = new ArrayList<String>();
		for (int i = 0; i < names.size(); i++)
			rc.add("[" + getName() + "]" + names.get(i));
		return rc;
	}

	public boolean canHandle(String cmdname)
	{
		List<String> cmdnames = m_cmdnames;
		if (cmdnames == null)
			return false;
		return cmdnames.contains(cmdname.toLowerCase());
	}

	protected final static String[] getPSStringArr(Object ps)
	{
		String[] rc = null;
		if (ps instanceof List)
		{
			List<?> lps = (List<?>) ps;
			rc = new String[lps.size()];
			for (int i = 0; i < lps.size(); i++)
				rc[i] = ObjUtil.toString(lps.get(i));
		}
		else if (ps instanceof String[])
		{
			rc = (String[]) ps;
		}
		else if (ps instanceof Object[])
		{
			Object[] aps = (Object[]) ps;
			rc = new String[((Object[]) ps).length];
			for (int i = 0; i < aps.length; i++)
				rc[i] = ObjUtil.toString(aps[i]);
		}
		else if (ps instanceof String)
		{
			rc = ProcessUtil.splitCommandArgs((String) ps);
		}
		return rc;
	}
}
