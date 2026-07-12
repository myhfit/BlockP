package bp.locale;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bp.util.BPPDUtil;
import bp.util.ClassUtil;
import bp.util.IOUtil;
import bp.util.ObjUtil;
import bp.util.Std;

public abstract class BPLocaleHelperBase<C extends BPLocaleConst, V extends BPLocaleVerb> implements BPLocaleHelper<C, V>
{
	protected volatile Map<Integer, Object> m_actps;
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
		return rc != null ? rc : (String) m_actps.get(act.ordinal() | (verb == null ? 0 : verb.getValue()));
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

	public abstract void initDefaults(Map<Integer, Object> actmap);

	protected void ensureInit()
	{
		if (m_inited)
			return;
		m_inited = true;
		Map<Integer, Object> actmap = new HashMap<Integer, Object>();
		try
		{
			initDefaults(actmap);
			loadLocales(actmap);
			m_actps = actmap;
		}
		catch (Exception e)
		{
			Std.err(e);
		}
	}

	protected void loadLocales(Map<Integer, Object> actmap)
	{
		String lstr = getCurrentLocale();
		if (lstr == null || lstr.length() == 0)
			return;
		Map<String, Object> cfs = new HashMap<String, Object>();
		List<String> ls = splitLocales(lstr);
		for (String l : ls)
		{
			Map<String, Object> sub = readLocaleData(l);
			if (sub != null)
				cfs.putAll(sub);
		}
		if (cfs.size() > 0)
			loadLocaleDatas(actmap, cfs);
	}
	
	protected List<String> splitLocales(String locale)
	{
		List<String> rc = new ArrayList<String>();
		locale = locale.replace("-", "_");
		int vi0 = locale.indexOf("_");
		int vi1 = locale.lastIndexOf("_");
		if (vi0 > -1)
		{
			if(vi1 != vi0)
			{
				rc.add(locale.substring(0, vi0));
				rc.add(locale.substring(0, vi0) + "_" + locale.substring(vi0 + 1, vi1));
				rc.add(locale.substring(0, vi0) + "_" + locale.substring(vi1 + 1));
				rc.add(locale);
			}
			else
			{
				rc.add(locale.substring(0, vi0));
				rc.add(locale);
			}
		}
		else
		{
			rc.add(locale);
		}
		return rc;
	}
	
	protected String getLocalePath()
	{
		return "bp/locale/";
	}
	
	protected Map<String, Object> readLocaleData(String locale)
	{
		byte[] bs = null;
		Map<String, Object> rc = null;
		List<URL> urls=ClassUtil.getResources(ClassUtil.getExtensionClassLoader(),getLocalePath() + getPackName() + "." + locale + ".bppd");
		for (URL url : urls)
		{
			try (InputStream in = url.openStream())
			{
				if (in != null)
				{
					bs = IOUtil.read(in);
					rc = BPPDUtil.read(bs);
				}
			}
			catch (Exception e)
			{
				Std.err(e);
			}
		}
		return rc;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void loadLocaleDatas(Map<Integer, Object> actmap, Map<String, Object> acts)
	{
		Map<Integer, Object> cfs = new LinkedHashMap<Integer, Object>();
		Map<String, Integer> kmap = ObjUtil.enumToMap((Class) getConstClass(), true);
		Map<String, V> vmap = new HashMap<>();
		{
			Class<V> cv = getVerbClass();
			if (cv != null)
			{
				for (V v : getVerbClass().getEnumConstants())
					vmap.put(v.name(), v);
			}
		}
		for (String k : acts.keySet())
		{
			int vi = k.indexOf("_");
			int vi2 = k.indexOf(".");
			if (vi2 < 0)
			{
				Object v = acts.get(k);
				String rk = k;
				Integer ki = kmap.get(rk);
				cfs.put(ki, v);
			}
			else if (vi < vi2)
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
		actmap.putAll(cfs);
	}

	public void reInit()
	{
		m_inited = false;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public C findConst(String name)
	{
		return (C) ObjUtil.enumValueOf((Class) getConstClass(), name);
	}

	protected abstract Class<C> getConstClass();

	protected abstract Class<V> getVerbClass();
}
