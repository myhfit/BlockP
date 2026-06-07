package bp.res;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import bp.util.Std;

public interface BPResourceIO extends BPResource
{
	default boolean isIO()
	{
		return true;
	}

	default <T> T useInputStream(Function<InputStream, T> inseg)
	{
		try (InputStream in = getInputStream())
		{
			if (in != null)
				return inseg.apply(in);
		}
		catch (IOException e)
		{
			Std.err(e);
		}
		return null;
	}

	default <T> T useOutputStream(Function<OutputStream, T> out)
	{
		try (OutputStream bos = getOutputStream())
		{
			T rc = out.apply(bos);
			return rc;
		}
		catch (IOException e)
		{
			Std.err(e);
		}
		return null;
	}

	<T extends InputStream> T getInputStream();

	<T extends OutputStream> T getOutputStream();

	boolean exists();
}
