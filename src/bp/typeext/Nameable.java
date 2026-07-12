package bp.typeext;

import java.util.function.Function;

import bp.locale.BPLocaleHelpers;
import bp.util.ObjUtil;

public interface Nameable
{
	String getName();

	public static String nameGetter(Object n)
	{
		return ((Nameable) n).getName();
	}

	public static Function<Object, String> nameTranslator(Class<?> cls, String dictprefix)
	{
		return obj ->
		{
			Nameable n = (Nameable) obj;
			String name = n.getName();
			return BPLocaleHelpers.translateByClass(cls, name, dictprefix);
		};
	}

	public static String joinName(Iterable<?> ns, String delimiter)
	{
		return ObjUtil.joinDatas(ns, delimiter, Nameable::nameGetter, false);
	}
}
