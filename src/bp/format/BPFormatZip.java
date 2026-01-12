package bp.format;

public class BPFormatZip implements BPFormat
{
	public final static String FORMAT_ZIP = "ZIP";
	public final static String MIME_ZIP = "application/zip";

	public String getName()
	{
		return FORMAT_ZIP;
	}

	public String[] getExts()
	{
		return new String[] { ".zip", MIME_ZIP };
	}

	public String getMIME()
	{
		return MIME_ZIP;
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		if (feature == BPFormatFeature.ARCHIVE)
			return true;
		if (feature == BPFormatFeature.ZIP)
			return true;
		if (feature == BPFormatFeature.TREE || feature == BPFormatFeature.PATHTREE)
			return true;
		return false;
	}
}