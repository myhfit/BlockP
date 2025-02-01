package bp.data;

import java.io.IOException;
import java.io.OutputStream;

import bp.data.BPDataConsumer.BPDataConsumerBase;
import bp.res.BPResourceIO;
import bp.util.Std;
import bp.util.TextUtil;

public class BPDataConsumerResourceWriter extends BPDataConsumerBase<Object>
{
	protected volatile BPResourceIO m_res;
	protected volatile OutputStream m_out;
	protected volatile String m_encoding;

	public BPDataConsumerResourceWriter(BPResourceIO res)
	{
		m_res = res;
	}

	public void setEncoding(String encoding)
	{
		m_encoding = encoding;
	}

	public void accept(Object t)
	{
		OutputStream out = m_out;
		if (t == null)
			return;
		if (t instanceof String)
		{
			writeBytes(out, TextUtil.fromString((String) t, m_encoding == null ? "utf-8" : m_encoding));
		}
		else if (t instanceof byte[])
		{
			writeBytes(out, (byte[]) t);
		}
	}

	protected void writeBytes(OutputStream out, byte[] bs)
	{
		try
		{
			out.write(bs);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void runSegment(Runnable seg)
	{
		setup();
		try
		{
			m_res.useOutputStream(out ->
			{
				m_out = out;
				try
				{
					seg.run();
				}
				finally
				{
					m_out = null;
					try
					{
						out.flush();
						out.close();
					}
					catch (IOException e)
					{
						Std.err(e);
					}
				}
				return null;
			});
			finish();
		}
		finally
		{
			clear();
		}
	}

	public String getInfo()
	{
		return "Write Resource";
	}
}
