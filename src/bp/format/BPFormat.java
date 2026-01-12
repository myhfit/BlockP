package bp.format;

public interface BPFormat
{
	String getName();

	String[] getExts();

	default String getMIME()
	{
		return null;
	}

	default boolean canCover(String ext)
	{
		return false;
	}

	default boolean checkFeature(BPFormatFeature feature)
	{
		return false;
	}
}
