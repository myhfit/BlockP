package bp.format;

public class BPFormatGZip implements BPFormat
{
	public final static String FORMAT_GZIP = "GZIP";

	public String getName()
	{
		return FORMAT_GZIP;
	}

	public String[] getExts()
	{
		return new String[] { ".gz", "application/gzip" };
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		if (feature == BPFormatFeature.ARCHIVE)
			return true;
		if (feature == BPFormatFeature.TREE || feature == BPFormatFeature.PATHTREE)
			return true;
		return false;
	}
}