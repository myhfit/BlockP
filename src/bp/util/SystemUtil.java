package bp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import bp.os.BPOSFunctions;
import bp.os.BPOSHandlers;

public class SystemUtil
{
	private final static Map<String, Supplier<?>> S_INFOHANDLER = new ConcurrentHashMap<String, Supplier<?>>();
	private final static List<String> S_INFOHANDLERKEYS = new CopyOnWriteArrayList<String>(new String[] { "Properties", "Class Paths", "Class Paths(CL)", "Charsets" });

	static
	{
		S_INFOHANDLER.put("Properties", SystemUtil::getProperties);
		S_INFOHANDLER.put("Class Paths", SystemUtil::getClassPaths);
		S_INFOHANDLER.put("Class Paths(CL)", SystemUtil::getClassPaths2);
		S_INFOHANDLER.put("Charsets", SystemUtil::getCharsets);
	}

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
			SystemUtil.runSimpleProcess(SystemUtil.getKillCommand(pid, tree, force), null, false);
			return true;
		}
		return false;
	}

	public final static int runSimpleProcess(String[] cmds, String workdir, boolean usenative)
	{
		String cmd = cmds[0];
		String[] args = null;
		if (cmds.length > 1)
		{
			args = new String[cmds.length - 1];
			System.arraycopy(cmds, 1, args, 0, args.length);
		}
		return runSimpleProcess(cmd, workdir, args, usenative);
	}

	public final static int runSimpleProcess(String cmd, String workdir, String[] args, boolean usenative)
	{
		BPOSFunctions.RUN_SIMPLE func = null;
		if (usenative)
			func = BPOSHandlers.S_SIMPLERUN;
		if (func != null)
		{
			return func.run(cmd, workdir, args);
		}
		else
			return runSimpleProcess_Default(cmd, workdir, args, System.getProperty("file.encoding"));
	}

	public final static int runSimpleProcess_Default(String cmd, String workdir, String[] args, String encoding)
	{
		Process process = null;
		int r = 0;
		try
		{
			String wd = workdir;
			if (wd == null)
				wd = "";
			else if (!wd.endsWith(File.separator))
				wd += File.separator;
			String rawcmd = cmd + ((args == null) ? "" : (" " + String.join(" ", args)));
			process = Runtime.getRuntime().exec((workdir == null ? "" : workdir) + rawcmd);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), encoding));)
			{
				while (process.isAlive())
				{
					while (reader.ready())
					{
						reader.readLine();
					}
				}
				process.waitFor();
				process.destroyForcibly();
				r = process.exitValue();
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
		return r;
	}

	public final static String execSimpleProcess(String[] cmd, String encoding)
	{
		StringBuilder sb = new StringBuilder();
		Process process = null;
		try
		{
			process = Runtime.getRuntime().exec(cmd);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), encoding));)
			{
				boolean flag = false;
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

	private final static Map<String, Object> getProperties()
	{
		Properties props = System.getProperties();
		Map<String, Object> mo = new TreeMap<String, Object>();
		for (Object key : props.keySet())
		{
			mo.put((String) key, props.get(key));
		}
		return mo;
	}

	private final static List<String> getClassPaths()
	{
		List<String> urlstrs = new ArrayList<String>();
		String cpstr = System.getProperty("java.class.path");
		if (cpstr != null)
		{
			String[] cps = cpstr.split(File.pathSeparator);
			for (String cp : cps)
			{
				urlstrs.add(cp);
			}
		}
		List<URL> urls = ClassUtil.getExtensionClassLoader().getAllURLs();
		for (URL url : urls)
		{
			if ("file".equalsIgnoreCase(url.getProtocol()))
				urlstrs.add(url.getPath());
			else
				urlstrs.add(url.toString());
		}
		return urlstrs;
	}

	private final static List<String> getClassPaths2()
	{
		List<URL> urls = ClassUtil.getClassPaths();
		List<String> urlstrs = new ArrayList<String>();
		for (URL url : urls)
		{
			if ("file".equalsIgnoreCase(url.getProtocol()))
				urlstrs.add(url.getPath());
			else
				urlstrs.add(url.toString());
		}
		return urlstrs;
	}

	private final static List<String> getCharsets()
	{

		SortedMap<String, Charset> charsetmap = Charset.availableCharsets();
		List<String> charsetnames = new ArrayList<String>();
		for (String name : charsetmap.keySet())
		{
			Charset ch = charsetmap.get(name);
			charsetnames.add(ch.name() + "(" + ch.aliases().stream().collect(Collectors.joining(",")) + ")");
		}
		return charsetnames;
	}

	@SuppressWarnings("unchecked")
	public final static <T> T getSystemInfo(String systeminfokey)
	{
		Object rc = null;
		Supplier<?> h = S_INFOHANDLER.get(systeminfokey);
		if (h != null)
			rc = h.get();
		return (T) rc;
	}

	public final static List<String> getSystemInfoKeys()
	{
		return new ArrayList<String>(S_INFOHANDLERKEYS);
	}

	public final static void addSystemInfoHandler(String systeminfokey, Supplier<?> handler)
	{
		S_INFOHANDLER.put(systeminfokey, handler);
		if (!S_INFOHANDLERKEYS.contains(systeminfokey))
			S_INFOHANDLERKEYS.add(systeminfokey);
	}

	public enum SystemOS
	{
		Windows, Linux, Mac, Other, Unknown
	}
}
