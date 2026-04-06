package bp.locale;

public interface BPLocaleConstSimple extends BPLocaleConstDirect
{
	default String getValue(int flag)
	{
		return getNormalName();
	}
}
