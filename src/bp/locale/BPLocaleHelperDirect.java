package bp.locale;

import java.util.HashMap;
import java.util.Map;

public abstract class BPLocaleHelperDirect<C extends BPLocaleConstDirect, V extends BPLocaleVerb> extends BPLocaleHelperBase<C, V>
{
	public void initDefaults(Map<Integer, Object> actmap)
	{
		Map<Integer, Object> cfs = new HashMap<>();
		int[] verbs = null;
		{
			Class<V> cv = getVerbClass();
			if (cv != null)
			{
				V[] ecs = cv.getEnumConstants();
				verbs = new int[ecs.length];
				for (int i = 0; i < ecs.length; i++)
				{
					verbs[i] = ecs[i].getValue();
				}
			}
			if (verbs == null)
				verbs = new int[] { 0 };
		}

		for (C c : getConstClass().getEnumConstants())
		{
			int x = c.ordinal();
			for (int v : verbs)
				cfs.put(x | v, c.getValue(v));
		}
		actmap.putAll(cfs);
	}

	public final static <C extends BPLocaleConstDirect, V extends BPLocaleVerb> BPLocaleHelperDirect<C, V> createHelper(Class<C> c, Class<V> v, String packname, String localepath)
	{
		return new BPLocaleHelperDirectImpl<>(c, v, packname, localepath);
	}

	static class BPLocaleHelperDirectImpl<C extends BPLocaleConstDirect, V extends BPLocaleVerb> extends BPLocaleHelperDirect<C, V>
	{
		private Class<C> m_cc;
		private Class<V> m_cv;
		private String m_lpath;

		public BPLocaleHelperDirectImpl(Class<C> c, Class<V> v, String packname, String localepath)
		{
			m_cc = c;
			m_cv = v;
			m_packname = packname;
			m_lpath = localepath;
		}

		public String getPackName()
		{
			return m_packname;
		}

		protected Class<C> getConstClass()
		{
			return m_cc;
		}

		protected Class<V> getVerbClass()
		{
			return m_cv;
		}

		protected String getLocalePath()
		{
			return m_lpath != null ? m_lpath : super.getLocalePath();
		}
	}
}
