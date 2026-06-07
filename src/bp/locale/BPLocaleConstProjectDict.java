package bp.locale;

//Project Dynamic Dictionary
public enum BPLocaleConstProjectDict implements BPLocaleConstSimple
{
	S,
	;

	public final static String PACK_PRJDICT = "prjd";

	public String getPackName()
	{
		return PACK_PRJDICT;
	}

	public boolean needNormalizeCase()
	{
		return false;
	}
}
