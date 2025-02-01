package bp.format;

public class BPFormatXYData implements BPFormat
{
	public final static String FORMAT_XYDATA = "XYData";

	public String getName()
	{
		return FORMAT_XYDATA;
	}

	public String[] getExts()
	{
		return new String[] { "[XYDATA]" };
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		if (feature == BPFormatFeature.XYDATA)
			return true;
		return false;
	}
}