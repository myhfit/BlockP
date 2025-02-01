package bp.res;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

public interface BPResourceIO extends BPResource
{
	default boolean isIO()
	{
		return true;
	}

	<T> T useInputStream(Function<InputStream, T> in);

	<T> T useOutputStream(Function<OutputStream, T> out);
	
	boolean exists();
}
