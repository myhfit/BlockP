package bp.locale;

import java.util.List;
import java.util.Locale;

import bp.BPCore;

public interface BPLocaleHelper<C extends BPLocaleConst, VERB extends BPLocaleVerb>
{
	default String v(C act)
	{
		return v(act, null, null);
	}

	String v(C act, C alias, VERB verb);

	String getPackName();

	List<String> getKeys();

	void reInit();

	default String getCurrentLocale()
	{
		String l = BPCore.S_LOCALE;
		return l == null ? Locale.getDefault().toString() : l;
	}

	public final static BPLocaleHelper<?, ?> HELPER_NULL = new BPLocaleHelperEmpty();

	final static class BPLocaleHelperEmpty implements BPLocaleHelper<BPLocaleConst, BPLocaleVerb>
	{
		public String v(BPLocaleConst act, BPLocaleConst alias, BPLocaleVerb verb)
		{
			return null;
		}

		public String getPackName()
		{
			return null;
		}

		public List<String> getKeys()
		{
			return null;
		}

		public void reInit()
		{
		}
	}
}