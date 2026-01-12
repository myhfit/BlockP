package bp.format;

public class BPFormatJSON implements BPFormat
{
	public final static String FORMAT_JSON = "JSON";
	public final static String MIME_TEXT_JSON = "text/json";
	public final static String MIME_APP_JSON = "application/json";

	public String getName()
	{
		return FORMAT_JSON;
	}

	public String[] getExts()
	{
		return new String[] { ".json", MIME_APP_JSON, MIME_TEXT_JSON };
	}

	public String getMIME()
	{
		return MIME_APP_JSON;
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		if (feature == BPFormatFeature.TREE || feature == BPFormatFeature.OBJTREE)
			return true;
		return false;
	}
}