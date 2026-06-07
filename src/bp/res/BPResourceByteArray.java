package bp.res;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BPResourceByteArray extends BPResourceHolder implements BPResourceIO
{
	public BPResourceByteArray(byte[] bs, BPResource parent, String ext, String id, String name, boolean isleaf)
	{
		super(bs, parent, ext, id, name, isleaf);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends InputStream> T getInputStream()
	{
		if (m_data == null)
			return null;
		return (T) new ByteArrayInputStream((byte[]) m_data);
	}

	@SuppressWarnings("unchecked")
	public <T extends OutputStream> T getOutputStream()
	{
		return (T) new BPResourceByteArrayOutputStream();
	}

	public boolean exists()
	{
		return m_data != null;
	}

	public long getSize()
	{
		return m_data != null ? ((byte[]) m_data).length : 0;
	}
	
	protected class BPResourceByteArrayOutputStream extends ByteArrayOutputStream
	{
		protected volatile boolean closed = false;

		public void flush() throws IOException
		{
			BPResourceByteArray.this.m_data = buf;
			super.flush();
		}

		public void close() throws IOException
		{
			if (!closed)
			{
				BPResourceByteArray.this.m_data = buf;
				closed = true;
			}
			super.close();
		}
	}
}
