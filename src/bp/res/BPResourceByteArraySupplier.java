package bp.res;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	@SuppressWarnings("unchecked")
	public <T extends InputStream> T getInputStream()
	{
		if (m_data == null)
			return null;
		return (T) new ByteArrayInputStream((byte[]) getData());
	}

	@SuppressWarnings("unchecked")
	public <T extends OutputStream> T getOutputStream()
	{
		return (T) new BPResourceByteArraySupplierOutputStream();
	}

	public boolean exists()
	{
		return m_cb != null || m_data != null;
	}

	public void release()
	{
		m_cb = null;
		super.release();
	}

	public long getSize()
	{
		byte[] data = readFromSupplier();
		return data != null ? data.length : 0;
	}
	
	protected class BPResourceByteArraySupplierOutputStream extends ByteArrayOutputStream
	{
		protected volatile boolean closed = false;

		public void flush() throws IOException
		{
			BPResourceByteArraySupplier.this.m_data = buf;
			super.flush();
		}

		public void close() throws IOException
		{
			if (!closed)
			{
				BPResourceByteArraySupplier.this.m_data = buf;
				closed = true;
			}
			super.close();
		}
	}
}