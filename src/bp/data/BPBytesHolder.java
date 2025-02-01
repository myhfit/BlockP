package bp.data;

import java.io.UnsupportedEncodingException;

import bp.util.Std;

public class BPBytesHolder extends BPDataHolder implements BPTextContainer, BPDataContainerRandomAccess
{
	protected volatile String m_encoding = "utf-8";

	public int read(long pos, byte[] bs, int offset, int len)
	{
		byte[] datas = getBytes();
		int c = datas.length - (int) pos;
		if (c < 0)
			return -1;
		if (c == 0)
			return 0;
		System.arraycopy(datas, (int) pos, bs, offset, c);
		return c;
	}

	public void write(long pos, byte[] bs, int offset, int len)
	{
		byte[] datas = getBytes();
		int c = datas.length - offset;
		if (c <= 0)
			return;
		System.arraycopy(bs, (int) pos, datas, offset, c);
	}

	public long length()
	{
		byte[] bs = getBytes();
		return bs == null ? 0 : bs.length;
	}

	protected byte[] getBytes()
	{
		return (byte[]) m_data;
	}

	protected void setBytes(byte[] bs)
	{
		m_data = bs;
	}

	public String readAllText()
	{
		try
		{
			return new String(getBytes(), m_encoding);
		}
		catch (UnsupportedEncodingException e)
		{
			Std.err(e);
		}
		return null;
	}

	public boolean writeAllText(String text)
	{
		try
		{
			setBytes(text.getBytes(m_encoding));
			return true;
		}
		catch (UnsupportedEncodingException e)
		{
			Std.err(e);
		}
		return false;
	}

	public void setEncoding(String encoding)
	{
		m_encoding = encoding;
	}

	public String getEncoding()
	{
		return m_encoding;
	}
}
