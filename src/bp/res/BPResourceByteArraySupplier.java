package bp.res;

import static bp.util.Std.err;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.function.Supplier;

public class BPResourceByteArraySupplier extends BPResourceHolder implements BPResourceIO
{
	protected Supplier<byte[]> m_cb;

	public BPResourceByteArraySupplier(Supplier<byte[]> cb, BPResource parent, String ext, String id, String name, boolean isleaf)
	{
		super(new byte[0], parent, ext, id, name, isleaf);
		m_cb = cb;
	}

	@SuppressWarnings("unchecked")
	public <T> T getData()
	{
		return (T) readFromSupplier();
	}

	protected synchronized byte[] readFromSupplier()
	{
		if (m_cb != null)
		{
			byte[] bs = m_cb.get();
			m_data = bs;
			m_cb = null;
			return bs;
		}
		else
		{
			return (byte[]) m_data;
		}
	}

	public <T> T useInputStream(Function<InputStream, T> in)
	{
		try (ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) getData()))
		{
			return in.apply(bis);
		}
		catch (IOException e)
		{
			err(e);
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

	public void release()
	{
		m_cb = null;
		super.release();
	}
}