package bp.console;

import bp.util.Std;

public abstract class BPConsoleBase<C extends BPConsoleController> implements BPConsole
{
	protected String m_name;
	protected boolean m_strmode;
	protected C m_controller;

	public String getName()
	{
		return m_name;
	}

	public boolean isStringMode()
	{
		return m_strmode;
	}

	public BPConsoleController getController()
	{
		return m_controller;
	}

	public boolean start()
	{
		try
		{
			doStart();
			return true;
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return false;
	}

	protected abstract void doStart() throws Exception;

	protected abstract void doStop() throws Exception;

	public boolean stop()
	{
		try
		{
			doStop();
			return true;
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return false;
	}
}
