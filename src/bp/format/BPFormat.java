package bp.format;

public interface BPFormat
{
	String getName();

	String[] getExts();

	default boolean canCover(String ext)
	{
		return false;
	}

	default boolean checkFeature(BPFormatFeature feature)
	{
		return false;
	}
}
