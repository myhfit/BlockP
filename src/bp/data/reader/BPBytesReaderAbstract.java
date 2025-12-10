package bp.data.reader;

import java.nio.ByteOrder;

public abstract class BPBytesReaderAbstract implements BPBytesReader
{
	protected boolean m_bigendian;
	protected long m_pos;

	public BPBytesReaderAbstract()
	{
		m_bigendian = true;
		m_pos = 0;
	}

	public int position()
	{
		return (int) m_pos;
	}
	
	public long positionLong()
	{
		return m_pos;
	}

	public BPBytesReader position(int newpos)
	{
		m_pos = newpos;
		return this;
	}	

	public BPBytesReader positionLong(long newpos)
	{
		m_pos = newpos;
		return this;
	}

	public void reset()
	{
		m_pos = 0;
	}

	protected final static char ce(boolean big, char n)
	{
		return big ? n : Character.reverseBytes(n);
	}

	protected final static short ce(boolean big, short n)
	{
		return big ? n : Short.reverseBytes(n);
	}

	protected final static int ce(boolean big, int n)
	{
		return big ? n : Integer.reverseBytes(n);
	}

	protected final static long ce(boolean big, long n)
	{
		return big ? n : Long.reverseBytes(n);
	}

	public BPBytesReader order(ByteOrder bo)
	{
		m_bigendian = bo == ByteOrder.BIG_ENDIAN;
		return this;
	}

	public ByteOrder order()
	{
		return m_bigendian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
	}
}