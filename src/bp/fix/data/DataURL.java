package bp.fix.data;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Base64;

public class DataURL
{
	public String mediatype;
	public boolean base64;
	public Object content;
	public String charset;

	public DataURL(String path) throws URISyntaxException, UnsupportedEncodingException
	{
		int c0 = path.indexOf(",");
		if (c0 == -1)
		{
			throw new URISyntaxException(path, "incorrect data url");
		}
		String prefix = path.substring(0, c0);
		String contentstr = path.substring(c0 + 1);
		if (prefix.length() == 0)
		{
			prefix = "text/plain;charset=US-ASCII";
		}
		int c1 = prefix.indexOf(";");
		if (c1 > -1)
		{
			mediatype = prefix.substring(0, c1);
			String rp = prefix.substring(c1 + 1);
			if (mediatype.startsWith("text/"))
			{
				if (rp.startsWith("charset="))
				{
					charset = rp.substring(8).trim();
				}
				else
				{
					charset = "US-ASCII";
				}
			}
			else
				base64 = "base64".equals(rp);
		}
		else
		{
			mediatype = prefix;
		}
		if (base64)
		{
			content = Base64.getDecoder().decode(contentstr.getBytes());
		}
		else if (mediatype.startsWith("text/"))
		{
			content = URLDecoder.decode(contentstr, charset);
		}
		else
		{

		}
	}
}