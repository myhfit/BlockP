package bp.locale;

//Core Dynamic Dictionary
public enum BPLocaleConstCoreDict implements BPLocaleConstSimple
{
	S,
	;

	public final static String PACK_COREDICT = "cd";

	public String getPackName()
	{
		return PACK_COREDICT;
	}

	public boolean needNormalizeCase()
	{
		return false;
	}
}
