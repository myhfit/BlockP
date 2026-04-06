package bp.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import bp.util.ClassUtil;
import bp.util.ObjUtil;

public class BPLocaleHelpers
{
	public final static ConcurrentHashMap<String, BPLocaleHelper<?, ?>> S_LHS = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public final static <LH extends BPLocaleHelper<?, ?>> LH getHelper(String packname)
	{
		LH rc = (LH) S_LHS.get(packname);
		return (rc != null && rc == BPLocaleHelper.HELPER_NULL) ? null : rc;
	}

	@SuppressWarnings("unchecked")
	public final static <LH extends BPLocaleHelper<?, ?>> LH getHelperOrCreate(String key, Supplier<LH> creationseg)
	{
		LH rc = (LH) S_LHS.computeIfAbsent(key, k -> creationseg.get());
		return (rc != null && rc == BPLocaleHelper.HELPER_NULL) ? null : rc;
	}

	public final static void registerHelper(BPLocaleHelper<?, ?> helper)
	{
		S_LHS.put(helper.getPackName(), helper);
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

	public final static <T, C extends BPLocaleConst, V extends BPLocaleVerb> T getValue(C key)
	{
		return getValue(key, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final static <T> T getValueReflect(String classname, String key)
	{
		Class<?> c = ClassUtil.getTClass(classname, ClassUtil.getExtensionClassLoader());
		if (c != null)
		{
			BPLocaleConst lc = (BPLocaleConst) ObjUtil.enumValueOf((Class) c, key);
			if (lc != null)
				return getValue(lc);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public final static <T, C extends BPLocaleConst, V extends BPLocaleVerb> T getValue(C key, V verb)
	{
		T rc = null;
		BPLocaleHelper<C, V> helper = getHelper(key.getPackName());
		if (helper != null)
			rc = (T) helper.v(key, null, verb);
		if (rc == null)
			return (T) key.getNormalName();
		return rc;
	}

	@SuppressWarnings("unchecked")
	public final static <C extends BPLocaleConstDirect> String translate(C key, String txt)
	{
		String pname = key.getPackName();
		BPLocaleHelperDict<C> helper = getHelperOrCreate(pname, () -> new BPLocaleHelperDict<C>((Class<C>) key.getClass(), key.getPackName()));
		String rc = null;
		if (helper != null)
			rc = helper.v(txt);
		if (rc == null)
			rc = txt;
		return rc;
	}

	public final static String translateByClassTree(Class<?> cls, String txt)
	{
		String rc = ClassUtil.tryLoopSuperClass(c -> translateByClassInner(c, txt, false), cls, Object.class);
		return rc != null ? rc : txt;
	}

	public final static String translateByClass(Class<?> cls, String txt)
	{
		String rc = translateByClassInner(cls, txt, true);
		return rc == null ? txt : rc;
	}

	@SuppressWarnings("unchecked")
	private final static String translateByClassInner(Class<?> cls, String txt, boolean load)
	{
		String clsname = cls.getName();
		BPLocaleHelper<?, ?> helper = null;
		helper = load ? getHelperOrCreate(clsname, () -> new BPLocaleHelperDict.BPLocaleHelperDictClass(cls.getName())) : getHelper(clsname);
		return helper == null ? null : ((BPLocaleHelperDict<BPLocaleConstDirect>) helper).v(txt);
	}
}
