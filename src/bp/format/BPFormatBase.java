package bp.format;

public abstract class BPFormatBase implements BPFormat
{
	public boolean equals(Object other)
	{
		if (other != null && other instanceof BPFormat)
			return getName().equals(((BPFormat) other).getName());
		return false;
	}

	public int hashCode()
	{
		return getName().hashCode();
	}
}
