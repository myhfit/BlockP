package bp.format;

public class BPFormatTSV implements BPFormatDSV
{
	public final static String FORMAT_TSV = "TSV";

	public String getName()
	{
		return FORMAT_TSV;
	}

	public String[] getExts()
	{
		return new String[] { ".tsv" };
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		if (feature == BPFormatFeature.TEXT || feature == BPFormatFeature.XYDATA || feature == BPFormatFeature.DSV)
			return true;
		return false;
	}

	public String getDelimiter()
	{
		return "\t";
	}
}