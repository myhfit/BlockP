package bp.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BPLocaleHelpers
{
	public final static Map<String, BPLocaleHelper<?, ?>> S_LHS = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public final static <LH extends BPLocaleHelper<?, ?>> LH getHelper(String packname)
	{
		return (LH) S_LHS.get(packname);
	}

	public final static void registerHelper(String packname, BPLocaleHelper<?, ?> helper)
	{
		S_LHS.put(packname, helper);
	}

	public final static List<BPLocaleHelper<?, ?>> listHelpers()
	{
		Map<String, BPLocaleHelper<?, ?>> helpers = new HashMap<String, BPLocaleHelper<?, ?>>(S_LHS);
		List<BPLocaleHelper<?, ?>> rc = new ArrayList<BPLocaleHelper<?, ?>>();
		for (BPLocaleHelper<?, ?> h : helpers.values())
		{
			if (!rc.contains(h))
				rc.add(h);
		}
		return rc;
	}
}
