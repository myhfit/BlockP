package bp.format;

public class BPFormatText implements BPFormat
{
	public final static String FORMAT_TEXT = "TEXT";
	public final static String MIME_TEXT = "text/plain";

	public String getName()
	{
		return FORMAT_TEXT;
	}

	public String[] getExts()
	{
		return new String[] { ".txt", ".text", MIME_TEXT };
	}

	public boolean canCover(String ext)
	{
		if (ext.indexOf("text/") == 0)
			return true;
		if ("application/html".equals(ext))
			return true;
		return false;
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		return feature == BPFormatFeature.TEXT;
	}
}
