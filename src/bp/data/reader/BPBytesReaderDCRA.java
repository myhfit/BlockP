package bp.data.reader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bp.data.BPDataContainerRandomAccess;
import bp.env.BPEnvCommon;
import bp.env.BPEnvManager;
import bp.util.ObjUtil;

public class BPBytesReaderDCRA extends BPBytesReaderAbstract
{
	protected ByteBuffer m_bb;
	protected int m_blocksize;
	protected BPDataContainerRandomAccess m_dc;
	protected long m_len;
	protected long m_blockstart;
	protected int m_cursize;

	public BPBytesReaderDCRA(BPDataContainerRandomAccess dc)
	{
		m_blocksize = ObjUtil.toInt(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_RAWIO_BLOCKSIZE), 4096);
		m_dc = dc;
		m_len = m_dc.length();
		m_bb = ByteBuffer.allocate(m_blocksize);
		load(0);
	}

	protected void load(long pos)
	{
		// Std.debug("load pos->"+pos);
		m_bb.position(0);
		long l = m_len;
		long s = l - pos;
		if (s <= 0)
			return;
		int blocksize = (int) Math.min(m_blocksize, s);
		byte[] bs = new byte[blocksize];
		m_dc.read(pos, bs, 0, blocksize);
		m_bb = ByteBuffer.wrap(bs);
		m_bb.order(m_bigendian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		m_blockstart = pos;
	}

	protected void checkLoad(long len)
	{
		long end = m_pos + len;
		if (end > m_len)
			throw new IndexOutOfBoundsException();
		if (end > m_blockstart + m_blocksize)
		{
			// Std.debug("load:" + m_pos);
			load(m_pos);
		}
		else
		{
			// Std.debug("read:" + m_pos + "+" + len);
		}
	}

	protected void checkLoad(long pos, int size)
	{
		long end = pos + size;
		if (end > m_len)
			throw new IndexOutOfBoundsException();
		if (end > m_blockstart + m_blocksize)
		{
			load(pos);
		}
	}

	public byte get()
	{
		checkLoad(1);
		m_pos++;
		return m_bb.get();
	}

	public byte get(int index)
	{
		return getBsAt(index, 1)[0];
	}

	public char getChar()
	{
		checkLoad(2);
		m_pos += 2;
		return m_bb.getChar();
	}

	protected byte[] getBsAt(long pos, int size)
	{
		if (pos + size > m_len)
			throw new IndexOutOfBoundsException();
		byte[] bs = new byte[size];
		m_dc.read(pos, bs, 0, size);
		return bs;
	}

	protected ByteBuffer getBBAt(long pos, int size)
	{
		byte[] bs = getBsAt(pos, size);
		ByteBuffer bb = ByteBuffer.wrap(bs);
		bb.order(m_bigendian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		return bb;
	}

	public char getChar(int index)
	{
		return getBBAt(index, 2).getChar();
	}

	public short getShort()
	{
		checkLoad(2);
		m_pos += 2;
		return m_bb.getShort();
	}

	public short getShort(int index)
	{
		return getBBAt(index, 2).getShort();
	}

	public int getInt()
	{
		checkLoad(4);
		m_pos += 4;
		return m_bb.getInt();
	}

	public int getInt(int index)
	{
		return getBBAt(index, 4).getInt();
	}

	public long getLong()
	{
		checkLoad(8);
		m_pos += 8;
		return m_bb.getLong();
	}

	public long getLong(int index)
	{
		return getBBAt(index, 8).getLong();
	}

	public float getFloat()
	{
		checkLoad(4);
		m_pos += 4;
		return m_bb.getFloat();
	}

	public float getFloat(int index)
	{
		return getBBAt(index, 4).getFloat();
	}

	public double getDouble()
	{
		checkLoad(8);
		m_pos += 8;
		return m_bb.getDouble();
	}

	public double getDouble(int index)
	{
		return getBBAt(index, 8).getDouble();
	}

	public void get(byte[] bs)
	{
		int len = bs.length;
		if ((m_pos + len) > m_len)
			throw new IndexOutOfBoundsException();
		long pos = m_pos;
		m_pos += len;
		if (m_pos > m_blockstart + m_blocksize)
		{
			m_dc.read(pos, bs, 0, len);
			load(pos);
		}
		else
		{
			m_bb.get(bs);
		}
	}

	public BPBytesReader order(ByteOrder bo)
	{
		super.order(bo);
		m_bb.order(bo);
		return this;
	}

	public BPBytesReader position(int newpos)
	{
		// Std.debug("repos:"+newpos);
		int delta = (int) (newpos - m_pos);
		m_pos = newpos;
		ByteBuffer bb = m_bb;
		checkLoad(1);
		if (bb == m_bb)
		{
			m_bb.position(m_bb.position() + delta);
		}
		return this;
	}
}
