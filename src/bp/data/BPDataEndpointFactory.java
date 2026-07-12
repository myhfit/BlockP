package bp.data;

import java.util.List;

import bp.typeext.Nameable;

public interface BPDataEndpointFactory extends Nameable
{
	String getName();

	<D> BPDataConsumer<D> create(String formatname);

	default boolean refuseSelectFormat()
	{
		return false;
	}

	List<String> getSupportedFormats();

	default boolean canHandle(String formatname)
	{
		return getSupportedFormats().contains(formatname);
	}
}
