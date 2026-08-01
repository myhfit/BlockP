package bp.typeext;

import java.util.function.Function;

import bp.locale.BPLocaleHelpers;
import bp.util.ObjUtil;

public interface Nameable
{
	String getName();

	public static Function<Object, String> nameTranslator(Class<?> cls, String dictprefix)
	{
		return obj -> BPLocaleHelpers.translateByClass(cls, ((Nameable) obj).getName(), dictprefix);
	}

	public static String joinName(Iterable<? extends Nameable> ns, String delimiter)
	{
		return ObjUtil.joinDatas(ns, delimiter, Nameable::getName, false);
	}
}
