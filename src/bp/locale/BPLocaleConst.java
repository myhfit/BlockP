package bp.locale;

public interface BPLocaleConst
{
	int ordinal();

	String name();

	String getPackName();

	default String getNormalName()
	{
		String n = name();
		if (needSwapSpace())
			n = n.replace('_', ' ');
		if (needNormalizeCase() && n.length() > 1)
			n = n.substring(0, 1).toUpperCase() + n.substring(1).toLowerCase();
		return n;
	}

	default boolean needSwapSpace()
	{
		return true;
	}

	default boolean needNormalizeCase()
	{
		return true;
	}

	default String text()
	{
		return BPLocaleHelpers.getValue(this);
	}
}
