package bp.format;

public class BPFormatCSV implements BPFormatDSV
{
	public final static String FORMAT_CSV = "CSV";

	public String getName()
	{
		return FORMAT_CSV;
	}

	public String[] getExts()
	{
		return new String[] { ".csv", "text/csv" };
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
