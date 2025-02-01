package bp.data;

import java.util.List;

public interface BPDataEndpointFactory
{
	String getName();

	<D> BPDataConsumer<D> create(String formatname);

	List<String> getSupportedFormats();

	default boolean canHandle(String formatname)
	{
		return getSupportedFormats().contains(formatname);
	}
}
