package bp.format;

public class BPFormatDir implements BPFormat
{
	public final static String FORMAT_DIR = "Directory";
	public final static String EXT_DIR = "[DIR]";

	public String getName()
	{
		return FORMAT_DIR;
	}

	public String[] getExts()
	{
		return new String[] { EXT_DIR };
	}

	public boolean canCover(String ext)
	{
		return false;
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		if (feature == BPFormatFeature.TREE || feature == BPFormatFeature.PATHTREE)
			return true;
		return false;
	}
}
