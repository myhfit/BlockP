package bp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemUtil
{
	public final static String getSystemEncoding()
	{
		String rc = System.getProperty("native.encoding");
		if (rc == null || rc.length() == 0)
			rc = System.getProperty("sun.jnu.encoding");
		return rc;
	}

	public final static String getShellName()
	{
		SystemOS os = getOS();
		switch (os)
		{
			case Windows:
				return "cmd.exe";
			case Linux:
				return "bash";
			default:
				return null;
		}
	}

	public final static String[] getKillCommand(long pid, boolean tree, boolean force)
	{
		SystemOS os = getOS();
		switch (os)
		{
			case Windows:
				return new String[] { "taskkill.exe", "/PID", "" + pid, (tree ? "/T" : ""), (force ? "/F" : "") };
			case Linux:
				return new String[] { "kill", "-9 " + pid };
			default:
				return null;
		}
	}

	public final static boolean kill(Process p, boolean tree, boolean force)
	{
		Long pid = ClassUtil.callMethod(Process.class, "pid", new Class[0], p, true);
		return kill(pid, tree, force);
	}

	public final static boolean kill(Long pid, boolean tree, boolean force)
	{
		if (pid != null)
		{
			SystemUtil.runProcess(SystemUtil.getKillCommand(pid, tree, force), System.getProperty("file.encoding"));
			return true;
		}
		return false;
	}

	public final static String runProcess(String[] cmd, String encoding)
	{
		StringBuilder sb = new StringBuilder();
		Process process = null;
		try
		{
			process = Runtime.getRuntime().exec(cmd);
			boolean flag = false;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), encoding));)
			{
				String newline;
				while (process.isAlive())
				{
					while (reader.ready())
					{
						if (flag)
							sb.append("\n");
						else
							flag = true;
						newline = reader.readLine();
						if (newline != null)
							sb.append(newline);
					}
				}
				process.waitFor();
				process.destroyForcibly();
			}
			catch (Exception e2)
			{
				Std.err(e2);
			}
			finally
			{
			}
		}
		catch (IOException e)
		{
			Std.err(e);
		}
		finally
		{
			if (process != null)
				process.destroy();
		}
		return sb.toString();
	}

	public final static SystemOS getOS()
	{
		String osname = System.getProperty("os.name");
		if (osname == null)
			return SystemOS.Unknown;
		osname = osname.toLowerCase();
		if (osname.startsWith("linux"))
			return SystemOS.Linux;
		else if (osname.startsWith("windows"))
			return SystemOS.Windows;
		else if (osname.startsWith("mac"))
			return SystemOS.Mac;
		else
			return SystemOS.Other;
	}

	public enum SystemOS
	{
		Windows, Linux, Mac, Other, Unknown
	}
}
