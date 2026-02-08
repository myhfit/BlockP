package bp.format;

public class BPFormatBPPD implements BPFormat
{
	public final static String FORMAT_BPPD = "BlockP Packed Data";

	public String getName()
	{
		return FORMAT_BPPD;
	}

	public String[] getExts()
	{
		return new String[] { ".bppd" };
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		if (feature == BPFormatFeature.XYDATA || feature == BPFormatFeature.OBJTREE)
			return true;
		return false;
	}
}
