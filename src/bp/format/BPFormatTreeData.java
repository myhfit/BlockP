package bp.format;

public class BPFormatTreeData implements BPFormat
{
	public final static String FORMAT_TREEDATA = "TreeData";

	public String getName()
	{
		return FORMAT_TREEDATA;
	}

	public String[] getExts()
	{
		return new String[] { "[TREEDATA]" };
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		if (feature == BPFormatFeature.TREE || feature == BPFormatFeature.OBJTREE)
			return true;
		return false;
	}
}
