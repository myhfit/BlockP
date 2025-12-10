package bp.data.reader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BPBytesReaderBB implements BPBytesReader
{
	protected ByteBuffer m_bb;

	public BPBytesReaderBB(byte[] bs)
	{
		m_bb = ByteBuffer.wrap(bs);
	}

	public BPBytesReaderBB(byte[] bs, int offset, int length)
	{
		m_bb = ByteBuffer.wrap(bs, offset, length);
	}

	public byte get()
	{
		return m_bb.get();
	}

	public byte get(int index)
	{
		return m_bb.get(index);
	}

	public char getChar()
	{
		return m_bb.getChar();
	}

	public char getChar(int index)
	{
		return m_bb.getChar(index);
	}

	public short getShort()
	{
		return m_bb.getShort();
	}

	public short getShort(int index)
	{
		return m_bb.getShort(index);
	}

	public int getInt()
	{
		return m_bb.getInt();
	}

	public int getInt(int index)
	{
		return m_bb.getInt(index);
	}

	public long getLong()
	{
		return m_bb.getLong();
	}

	public long getLong(int index)
	{
		return m_bb.getLong(index);
	}

	public float getFloat()
	{
		return m_bb.getFloat();
	}

	public float getFloat(int index)
	{
		return m_bb.getFloat(index);
	}

	public double getDouble()
	{
		return m_bb.getDouble();
	}

	public double getDouble(int index)
	{
		return m_bb.getDouble(index);
	}

	public ByteOrder order()
	{
		return m_bb.order();
	}

	public BPBytesReader order(ByteOrder bo)
	{
		m_bb.order(bo);
		return this;
	}

	public int position()
	{
		return m_bb.position();
	}

	public long positionLong()
	{
		return m_bb.position();
	}

	public BPBytesReader position(int index)
	{
		m_bb.position(index);
		return this;
	}

	public BPBytesReader positionLong(long index)
	{
		m_bb.position((int) index);
		return this;
	}

	public void get(byte[] bs)
	{
		m_bb.get(bs);
	}
}
