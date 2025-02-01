package bp.streamhandler.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.UnknownServiceException;

import bp.fix.data.DataURL;
import bp.util.Std;

public class Handler extends URLStreamHandler
{
	protected URLConnection openConnection(URL url) throws IOException
	{
		return new DataURLConnection(url);
	}

	public static class DataURLConnection extends URLConnection
	{
		protected DataURLConnection(URL url)
		{
			super(url);
		}

		public void connect() throws IOException
		{
		}

		public InputStream getInputStream() throws IOException
		{
			if ("data".equals(url.getProtocol()))
			{
				String path = url.getPath();
				try
				{
					DataURL url = new DataURL(path);
					if (url.content != null && url.base64)
					{
						return new ByteArrayInputStream((byte[]) url.content);
					}
				}
				catch (UnsupportedEncodingException | URISyntaxException e)
				{
					Std.err(e);
				}
			}
			throw new UnknownServiceException("protocol doesn't support input");
		}

		public OutputStream getOutputStream() throws IOException
		{
			throw new UnknownServiceException("protocol doesn't support output");
		}
	}
}
