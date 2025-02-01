package bp.env;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import bp.BPCore;
import bp.config.BPConfigAdv;
import bp.config.BPConfigAdv.BPConfigAdvBase;
import bp.util.IOUtil;
import bp.util.TextUtil;

public class BPEnvs extends BPConfigAdvBase
{
	protected final static List<BPEnv> S_ENVS = new CopyOnWriteArrayList<>();
	protected final static String S_ENVFILE = ".bpenvs";

	protected void loadConfig(BPConfigAdv config)
	{
		byte[] bs = IOUtil.read(BPCore.getWorkspaceContext().getConfigRes(S_ENVFILE));
		Map<String, String> pmap = new HashMap<String, String>();
		if (bs != null)
		{
			String str = TextUtil.toString(bs, "utf-8");
			String[] lines = str.split("\n");
			for (String line : lines)
			{
				int vi = line.indexOf("=");
				String key, value;
				if (vi > -1)
				{
					key = line.substring(0, vi).trim();
					value = line.substring(vi + 1).trim();
				}
				else
				{
					key = line.trim();
					value = null;
				}
				pmap.put(key, value);
				config.put(key, value);
			}
		}
		BPEnvManager.setEnvs(pmap);
	}

	protected void saveConfig(BPConfigAdv config)
	{
		List<BPEnv> envs = BPEnvManager.listEnv();
		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		for (BPEnv env : envs)
		{
			String envname = env.getName();
			List<String> keys = env.listKeys();
			for (String key : keys)
			{
				if (flag)
					sb.append("\n");
				else
					flag = true;
				sb.append(envname + "|" + key);
				String value = env.getValue(key);
				if (value != null)
					sb.append("=" + value);
			}
		}
		IOUtil.write(BPCore.getWorkspaceContext().getConfigRes(S_ENVFILE, false), TextUtil.fromString(sb.toString(), "utf-8"));
	}

	public <S extends BPConfigAdv> Consumer<S> getConfigLoader()
	{
		return this::loadConfig;
	}

	public <S extends BPConfigAdv> void setConfigLoader(Consumer<S> loader)
	{
	}

	public <S extends BPConfigAdv> Consumer<S> getConfigPersister()
	{
		return this::saveConfig;
	}

	public <S extends BPConfigAdv> void setConfigPersister(Consumer<S> persister)
	{
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		List<BPEnv> envs = BPEnvManager.listEnv();
		envs.sort((a, b) ->
		{
			return a.getName().compareTo(b.getName());
		});
		rc.put("envs", envs);
		return rc;
	}

	protected Map<String, Object> createMap()
	{
		return new HashMap<String, Object>();
	}

	public boolean canUserConfig()
	{
		return true;
	}

	public String getConfigName()
	{
		return "Environments";
	}
}
