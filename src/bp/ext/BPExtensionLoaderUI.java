package bp.ext;

public interface BPExtensionLoaderUI extends BPExtensionLoader
{
	default boolean isUI()
	{
		return true;
	}
}
