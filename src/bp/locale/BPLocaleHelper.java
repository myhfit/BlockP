package bp.locale;

import java.util.List;

public interface BPLocaleHelper<C extends BPLocaleConst, VERB extends BPLocaleVerb>
{
	String v(C act, C alias, VERB verb);

	String getPackName();

	List<String> getKeys();
}