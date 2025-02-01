package bp.format;

public class BPFormatProject implements BPFormat
{
	public final static String FORMAT_PROJECT = "Project";
	public final static String EXT_PROJECT = "[PROJECT]";

	public String getName()
	{
		return FORMAT_PROJECT;
	}

	public String[] getExts()
	{
		return new String[] { EXT_PROJECT };
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
