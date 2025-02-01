package bp.data;

import static bp.util.Std.err;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import bp.res.BPResource;
import bp.res.BPResourceHolder;

public class BPDataHolder implements BPDataContainer
{
	protected volatile String m_title;
	protected volatile Object m_data;

	public void setTitle(String title)
	{
		m_title = title;
	}

	public void close()
	{
	}

	public void open()
	{
	}

	public void bind(BPResource res)
	{
		if (res != null && res instanceof BPResourceHolder)
		{
			setData(((BPResourceHolder) res).getData());
		}
	}

	public void unbind()
	{
	}

	public BPResource getResource()
	{
		return null;
	}

	public byte[] readAll()
	{
		byte[] bs = (byte[]) m_data;
		byte[] r = null;
		if (bs != null)
		{
			int len = bs.length;
			r = new byte[len];
			if (bs.length > 0)
				System.arraycopy(bs, 0, r, 0, len);
		}
		return r;
	}

	public boolean writeAll(byte[] bs)
	{
		if (bs != null)
		{
			int len = bs.length;
			byte[] r = new byte[len];
			if (bs.length > 0)
				System.arraycopy(bs, 0, r, 0, len);
			m_data = r;
		}
		return true;
	}

	public String getTitle()
	{
		return m_title;
	}

	public void setData(Object data)
	{
		m_data = data;
	}

	public Object getData()
	{
		return m_data;
	}

	public <T> T useInputStream(Function<InputStream, T> in)
	{
		try (ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) m_data))
		{
			return in.apply(bis);
		}
		catch (IOException e)
		{
			err(e);
		}
		return null;
	}

	public <T> T useOutputStream(Function<OutputStream, T> out)
	{
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream())
		{
			T rc = out.apply(bos);
			m_data = bos.toByteArray();
			return rc;
		}
		catch (IOException e)
		{
			err(e);
		}
		return null;
	}
}
