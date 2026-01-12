package bp.util;

public class CompareUtil
{
	public final static boolean compareValue(Object v0, Object v1, COMPARE_FLAGS flags)
	{
		if (flags.to_text)
		{
			v0 = ObjUtil.toString(v0, flags.empty_text);
			v1 = ObjUtil.toString(v1, flags.empty_text);
			return compareText((String) v0, (String) v1, flags);
		}
		Boolean b = checkNull(v0, v1);
		if (b != null)
			return b;
		if (v0.getClass() != v1.getClass())
			return false;
		return v0.equals(v1);
	}

	public final static boolean compareText(String v0, String v1, COMPARE_FLAGS flags)
	{
		Boolean b = checkNull(v0, v1);
		if (b != null)
			return b;
		return v0.equals(v1);
	}

	public final static Boolean checkNull(Object v0, Object v1)
	{
		boolean n0 = v0 == null;
		boolean n1 = v1 == null;
		if (n0 != n1)
			return false;
		if (n0)
			return true;
		return null;
	}

	public static class COMPARE_FLAGS
	{
		public boolean case_insensitive;
		public boolean to_text;
		public String empty_text;
	}
}
