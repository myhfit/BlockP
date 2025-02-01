package bp.format;

public class BPFormatJSON implements BPFormat
{
	public final static String FORMAT_JSON = "JSON";

	public String getName()
	{
		return FORMAT_JSON;
	}

	public String[] getExts()
	{
		return new String[] { ".json", "text/json", "application/json" };
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		if (feature == BPFormatFeature.TREE || feature == BPFormatFeature.OBJTREE)
			return true;
		return false;
	}
}