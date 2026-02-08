package bp.locale;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bp.util.BPPDUtil;
import bp.util.ClassUtil;
import bp.util.IOUtil;
import bp.util.ObjUtil;

public abstract class BPLocaleHelperBase<C extends BPLocaleConst, V extends BPLocaleVerb> implements BPLocaleHelper<C, V>
{
	protected Map<Integer, Object> m_actps;
	protected String m_packname;
	protected volatile boolean m_inited;

	public BPLocaleHelperBase()
	{
		m_actps = new HashMap<Integer, Object>();
		m_packname = getPackName();
	}

	public String v(C act, V verb)
	{
		ensureInit();
		String rc = getOverwriteValue(act, verb);
		return rc != null ? rc : (String) m_actps.get(act.ordinal() | verb.getValue());
	}

	protected String getOverwriteValue(C act, V verb)
	{
		return null;
	}

	public String v(C act, C alias, V verb)
	{
		if (alias == null)
			return v(act, verb);
		else
		{
			String rc = v(alias, verb);
			return rc != null ? rc : v(act, verb);
		}
	}

	public abstract void initDefaults();

	protected void ensureInit()
	{
		if (m_inited)
			return;
		m_inited = true;
		initDefaults();
		loadLocales();
	}

	protected void loadLocales()
	{
		String l = Locale.getDefault().toString();
		byte[] bs = null;
		try (InputStream in = ClassUtil.getExtensionClassLoader().getResourceAsStream("bp/locale/" + getPackName() + "." + l + ".bppd"))
		{
			if (in != null)
			{
				bs = IOUtil.read(in);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if (bs != null)
		{
			Map<String, Object> cfs = BPPDUtil.read(bs);
			loadLocaleDatas(cfs);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void loadLocaleDatas(Map<String, Object> acts)
	{
		Map<Integer, Object> cfs = new LinkedHashMap<Integer, Object>();
		Map<String, Integer> kmap = ObjUtil.enumToMap((Class) getConstClass(), true);
		Map<String, V> vmap = new HashMap<>();
		for (V v : getVerbClass().getEnumConstants())
			vmap.put(v.name(), v);
		for (String k : acts.keySet())
		{
			int vi = k.indexOf("_");
			if (vi > -1)
			{
				int vi2 = k.indexOf(".");
				if (vi < vi2)
				{
					Object v = acts.get(k);
					String rk = k.substring(0, vi2);
					String rv = k.substring(vi2 + 1);
					V verb = vmap.get(rv);
					Integer ki = kmap.get(rk);
					if (ki != null && verb != null)
						cfs.put(ki | verb.getValue(), v);
				}
			}
		}
		m_actps.putAll(cfs);
	}

	public void reInit()
	{
		m_inited = false;
		m_actps.clear();
	}

	public List<String> getKeys()
	{
		List<String> rc = new ArrayList<String>();
		List<Integer> keys = new ArrayList<Integer>(m_actps.keySet());
		Map<Integer, String> emap = new HashMap<Integer, String>();
		Map<Integer, String> vmap = new HashMap<Integer, String>();
		for (C e : getConstClass().getEnumConstants())
			emap.put(e.ordinal(), e.name());
		for (V v : getVerbClass().getEnumConstants())
			emap.put(v.getValue(), v.name());
		for (int k : keys)
		{
			int v = k & 0xF0000000;
			int n = k & 0x0FFFFFFF;
			String name = emap.get(n);
			if (name != null)
				rc.add(name + "." + vmap.get(v));
		}
		return rc;
	}

	protected abstract Class<C> getConstClass();

	protected abstract Class<V> getVerbClass();
}
