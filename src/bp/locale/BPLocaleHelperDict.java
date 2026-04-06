package bp.locale;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.util.BPPDUtil;
import bp.util.ClassUtil;
import bp.util.IOUtil;
import bp.util.Std;

public class BPLocaleHelperDict<C extends BPLocaleConstDirect> implements BPLocaleHelper<C, BPLocaleVerb>
{
	protected volatile Map<String, String> m_actps;
	protected String m_packname;
	protected String m_path;
	protected volatile boolean m_inited;
	protected volatile Class<C> m_cc;

	public BPLocaleHelperDict(Class<C> constclass, String packname)
	{
		m_actps = new HashMap<String, String>();
		m_packname = packname;
		m_cc = constclass;
	}

	public String v(C act, BPLocaleVerb verb)
	{
		ensureInit();
		return m_actps.get(act.getNormalName());
	}

	public String v(C act, C alias, BPLocaleVerb verb)
	{
		if (alias == null)
			return v(act, verb);
		else
		{
			String rc = v(alias, verb);
			return rc != null ? rc : v(act, verb);
		}
	}

	public String v(String txt)
	{
		ensureInit();
		return m_actps.get(txt);
	}

	public String getPackName()
	{
		return m_packname;
	}

	protected String getOverwriteValue(C act, BPLocaleVerb verb)
	{
		return null;
	}

	public void initDefaults(Map<String, String> actmap)
	{
		Map<String, String> cfs = new HashMap<>();
		C[] es = getConstClass().getEnumConstants();
		if (es != null)
		{
			for (C c : es)
			{
				String n = c.getNormalName();
				cfs.put(n, c.getValue(0));
			}
			actmap.putAll(cfs);
		}
	}

	protected void ensureInit()
	{
		if (m_inited)
			return;
		m_inited = true;
		Map<String, String> actmap = new HashMap<String, String>();
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

	protected void loadLocales(Map<String, String> actmap)
	{
		String lstr = getCurrentLocale();
		if (lstr == null || lstr.length() == 0)
			return;
		List<String> ls = splitLocales(lstr);
		for (String l : ls)
		{
			Map<String, String> sub = readLocaleData(l);
			if (sub != null)
				actmap.putAll(sub);
		}
	}

	protected List<String> splitLocales(String locale)
	{
		List<String> rc = new ArrayList<String>();
		locale = locale.replace("-", "_");
		int vi0 = locale.indexOf("_");
		int vi1 = locale.lastIndexOf("_");
		if (vi0 > -1)
		{
			if (vi1 != vi0)
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
		return m_path != null ? m_path : "bp/locale/";
	}

	protected String getLocaleResourceName()
	{
		return getPackName();
	}

	protected Map<String, String> readLocaleData(String locale)
	{
		byte[] bs = null;
		Map<String, String> rc = new HashMap<String, String>();
		List<URL> urls = ClassUtil.getResources(ClassUtil.getExtensionClassLoader(), getLocalePath() + getLocaleResourceName() + "." + locale + ".bppd");
		for (URL url : urls)
		{
			try (InputStream in = url.openStream())
			{
				if (in != null)
				{
					bs = IOUtil.read(in);
					rc.putAll(BPPDUtil.read(bs));
				}
			}
			catch (Exception e)
			{
				Std.err(e);
			}
		}
		return rc;
	}

	public void reInit()
	{
		m_inited = false;
	}

	public List<String> getKeys()
	{
		return new ArrayList<String>(m_actps.keySet());
	}

	protected Class<C> getConstClass()
	{
		return m_cc;
	}

	public static class BPLocaleHelperDictClass extends BPLocaleHelperDict<BPLocaleConstDirect>
	{
		public BPLocaleHelperDictClass(String clsname)
		{
			super(BPLocaleConstDirect.class, clsname);
		}

		public final static String transClassName(String clsname)
		{
			return clsname.replace('.', '_');
		}

		protected String getLocalePath()
		{
			return "bp/locale/cls/";
		}

		protected String getLocaleResourceName()
		{
			return transClassName(getPackName());
		}
	}
}
