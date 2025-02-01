package bp.format;

public class BPFormatUnknown implements BPFormat
{
	public final static String FORMAT_NA = "N/A";
	public final static String MIME_NA = "application/octet-stream";

	public String getName()
	{
		return FORMAT_NA;
	}

	public String[] getExts()
	{
		return null;
	}
}