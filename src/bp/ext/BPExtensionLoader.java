package bp.ext;

import bp.context.BPFileContext;

public interface BPExtensionLoader
{
	String getName();

	boolean isUI();

	String getUIType();

	String[] getParentExts();

	String[] getDependencies();

	default String getInfo()
	{
		String[] pars = getParentExts();
		String parstr = "";
		if (pars != null && pars.length > 0)
		{
			StringBuilder sb = new StringBuilder();
			for (String par : pars)
			{
				if (sb.length() > 0)
					sb.append(",");
				sb.append(par);
			}
			parstr = "@[" + sb.toString() + "]";
		}
		return getName() + (isUI() ? "(UI)" : "") + parstr;
	}

	default void preload()
	{
	}

	default void install(BPFileContext context)
	{
	}

	default void uninstall(BPFileContext context)
	{
	}
}
