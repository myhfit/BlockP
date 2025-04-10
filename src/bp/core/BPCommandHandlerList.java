package bp.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.data.BPCommand;
import bp.data.BPCommandResult;

public class BPCommandHandlerList implements BPCommandHandler
{
	protected List<BPCommandHandler> m_chs = new CopyOnWriteArrayList<BPCommandHandler>();

	public BPCommandResult call(BPCommand cmd)
	{
		List<BPCommandHandler> chs = new ArrayList<BPCommandHandler>(m_chs);
		String cmdname = cmd.name;
		for (BPCommandHandler ch : chs)
		{
			if (ch.canHandle(cmdname))
				return ch.call(cmd);
		}
		return null;
	}

	public boolean canHandle(String cmdname)
	{
		List<BPCommandHandler> chs = new ArrayList<BPCommandHandler>(m_chs);
		for (BPCommandHandler ch : chs)
		{
			if (ch.canHandle(cmdname))
				return true;
		}
		return false;
	}

	public String getName()
	{
		return null;
	}

	public void addHandler(BPCommandHandler child)
	{
		m_chs.add(child);
	}

	public boolean removeHandler(String name)
	{
		List<BPCommandHandler> chs = new ArrayList<BPCommandHandler>(m_chs);
		BPCommandHandler selch = null;
		for (BPCommandHandler ch : chs)
		{
			if (name.equals(ch.getName()))
				selch = ch;
		}
		if (selch != null)
			return chs.remove(selch);
		return false;
	}

	public List<String> getCommandNames()
	{
		List<String> rc = new ArrayList<String>();

		List<BPCommandHandler> chs = new ArrayList<BPCommandHandler>(m_chs);
		for (BPCommandHandler ch : chs)
		{
			List<String> subr = ch.getCommandNames();
			if (subr != null)
				rc.addAll(subr);
		}

		return rc;
	}
}
