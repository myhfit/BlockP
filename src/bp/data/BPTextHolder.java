package bp.data;

import java.io.UnsupportedEncodingException;

import bp.util.Std;

public class BPTextHolder extends BPDataHolder implements BPTextContainer
{
	protected String m_encoding = "utf-8";

	public String readAllText()
	{
		Object data = m_data;
		String rc = null;
		if (data != null)
		{
			if (data instanceof byte[])
			{
				byte[] bs = (byte[]) data;
				if (bs != null)
				{
					try
					{
						rc = new String(bs, m_encoding);
						new String(new byte[] {}, m_encoding);// clear
																// threadlocal
																// cache
					}
					catch (UnsupportedEncodingException e)
					{
						Std.err(e);
					}
				}
			}
			else if (data instanceof String)
			{
				rc = (String) data;
			}
		}
		return rc;
	}

	public boolean writeAllText(String text)
	{
		m_data = text;
		return true;
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
