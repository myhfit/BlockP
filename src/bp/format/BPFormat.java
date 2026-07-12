package bp.format;

import bp.typeext.Nameable;

public interface BPFormat extends Nameable
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
