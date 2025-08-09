package bp.res;

import static bp.util.Std.err;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

public class BPResourceByteArray extends BPResourceHolder implements BPResourceIO
{
	public BPResourceByteArray(byte[] bs, BPResource parent, String ext, String id, String name, boolean isleaf)
	{
		super(bs, parent, ext, id, name, isleaf);
	}

	public <T> T useInputStream(Function<InputStream, T> in)
	{
		if (m_data != null)
		{
			try (ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) m_data))
			{
				return in.apply(bis);
			}
			catch (IOException e)
			{
				err(e);
			}
		}
		return null;
	}

	public <T> T useOutputStream(Function<OutputStream, T> out)
	{
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream())
		{
			T rc = out.apply(bos);
			m_data = bos.toByteArray();
			return rc;
		}
		catch (IOException e)
		{
			err(e);
		}
		return null;
	}

	public boolean exists()
	{
		return m_data != null;
	}

	public long getSize()
	{
		return m_data != null ? ((byte[]) m_data).length : 0;
	}
}
