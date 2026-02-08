package bp.data.writer;

import java.nio.ByteOrder;

public abstract class BPBytesWriterAbstract implements BPBytesWriter
{
	protected boolean m_bigendian;

	public BPBytesWriterAbstract()
	{
		m_bigendian = true;
	}

	public ByteOrder order()
	{
		return m_bigendian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
	}

	public BPBytesWriter order(ByteOrder bo)
	{
		m_bigendian = bo == ByteOrder.BIG_ENDIAN;
		return this;
	}
}
