package bp.data;

import java.io.UnsupportedEncodingException;

import bp.util.Std;

public class BPTextContainerBase extends BPDataContainerBase implements BPTextContainer
{
	protected volatile String m_encoding = "utf-8";

	public String readAllText()
	{
		byte[] bs = readAll();
		String rc = null;
		if (bs != null)
		{
			try
			{
				rc = new String(bs, m_encoding);
				new String(new byte[] {}, m_encoding);// clear threadlocal cache
			}
			catch (UnsupportedEncodingException e)
			{
				Std.err(e);
			}
		}
		return rc;
	}

	public boolean writeAllText(String text)
	{
		try
		{
			byte[] bs = text.getBytes(m_encoding);
			return writeAll(bs);
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
