package bp.format;

public class BPFormatCSV implements BPFormatDSV
{
	public final static String FORMAT_CSV = "CSV";
	public final static String MIME_CSV = "text/csv";

	public String getName()
	{
		return FORMAT_CSV;
	}

	public String[] getExts()
	{
		return new String[] { ".csv", MIME_CSV };
	}

	public String getMIME()
	{
		return MIME_CSV;
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		if (feature == BPFormatFeature.TEXT || feature == BPFormatFeature.XYDATA || feature == BPFormatFeature.DSV)
			return true;
		return false;
	}

	public String getDelimiter()
	{
		return ",";
	}
}
