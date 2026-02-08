package bp.data.writer;

import java.io.IOException;
import java.io.OutputStream;

public class BPBytesWriterStream extends BPBytesWriterAbstract
{
	protected OutputStream m_out;
	protected long m_pos;

	public BPBytesWriterStream(OutputStream out)
	{
		m_out = out;
	}

	public BPBytesWriter put(byte v)
	{
		R(() ->
		{
			m_out.write(v);
			m_pos++;
		});
		return this;
	}

	public BPBytesWriter putShort(short v)
	{
		OutputStream out = m_out;
		R(() ->
		{
			if (m_bigendian)
			{
				out.write((v >> 8) & 0xFF);
				out.write(v & 0xFF);
			}
			else
			{
				out.write(v & 0xFF);
				out.write((v >> 8) & 0xFF);
			}
			m_pos += 4;
		});
		return this;
	}

	public BPBytesWriter putInt(int v)
	{
		OutputStream out = m_out;
		R(() ->
		{
			if (m_bigendian)
			{
				out.write((v >> 24) & 0xFF);
				out.write((v >> 16) & 0xFF);
				out.write((v >> 8) & 0xFF);
				out.write(v & 0xFF);
			}
			else
			{
				int v2 = v;
				out.write(v2 & 0xFF);
				v2 = v2 >> 8;
				out.write(v2 & 0xFF);
				v2 = v2 >> 8;
				out.write(v2 & 0xFF);
				v2 = v2 >> 8;
				out.write(v2 & 0xFF);
			}
			m_pos += 4;
		});
		return this;
	}

	public BPBytesWriter putLong(long v)
	{
		OutputStream out = m_out;
		R(() ->
		{
			if (m_bigendian)
			{
				out.write((int) ((v >> 56) & 0xFF));
				out.write((int) ((v >> 48) & 0xFF));
				out.write((int) ((v >> 40) & 0xFF));
				out.write((int) ((v >> 32) & 0xFF));
				out.write((int) ((v >> 24) & 0xFF));
				out.write((int) ((v >> 16) & 0xFF));
				out.write((int) ((v >> 8) & 0xFF));
				out.write((int) (v & 0xFF));
			}
			else
			{
				out.write((int) (v & 0xFF));
				out.write((int) ((v >> 8) & 0xFF));
				out.write((int) ((v >> 16) & 0xFF));
				out.write((int) ((v >> 24) & 0xFF));
				out.write((int) ((v >> 32) & 0xFF));
				out.write((int) ((v >> 40) & 0xFF));
				out.write((int) ((v >> 48) & 0xFF));
				out.write((int) ((v >> 56) & 0xFF));
			}
			m_pos += 8;
		});
		return this;
	}

	public BPBytesWriter putFloat(float v)
	{
		return putInt(Float.floatToIntBits(v));
	}

	public BPBytesWriter putDouble(double v)
	{
		return putLong(Double.doubleToLongBits(v));
	}

	public int position()
	{
		return (int) m_pos;
	}

	public long positionLong()
	{
		return m_pos;
	}

	public BPBytesWriter position(int newpos)
	{
		int m = (int) (newpos - m_pos);
		if (m > 0)
		{
			R(() ->
			{
				m_out.write(new byte[(int) m]);
				m_pos = newpos;
			});
		}
		return this;
	}

	public BPBytesWriter positionLong(long newpos)
	{
		int m = (int) (newpos - m_pos);
		if (m > 0)
		{
			R(() ->
			{
				m_out.write(new byte[(int) m]);
				m_pos = newpos;
			});
		}
		return this;
	}

	public BPBytesWriter put(byte[] bs)
	{
		R(() ->
		{
			m_out.write(bs);
			m_pos += bs.length;
		});
		return this;
	}

	protected final static void R(ERunnable seg)
	{
		try
		{
			seg.run();
		}
		catch (Exception e)
		{
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(e);
		}
	}

	private static interface ERunnable
	{
		public void run() throws IOException;
	}
}
