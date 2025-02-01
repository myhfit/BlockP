package bp.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import bp.res.BPResource;
import bp.res.BPResourceIO;
import bp.util.IOUtil;

public class BPDataContainerBase implements BPDataContainer
{
	protected volatile BPResource m_res;

	public void bind(BPResource res)
	{
		m_res = res;
	}

	public void unbind()
	{
		m_res.release();
		m_res = null;
	}

	public byte[] readAll()
	{
		byte[] bs = null;
		BPResource res = m_res;
		if (res.isIO())
		{
			bs = ((BPResourceIO) m_res).useInputStream((in) ->
			{
				return IOUtil.read(in);
			});
		}
		return bs;
	}

	public boolean writeAll(byte[] bs)
	{
		Boolean flag = false;
		BPResource res = m_res;
		if (res.isIO())
		{
			flag = ((BPResourceIO) m_res).useOutputStream((out) ->
			{
				return IOUtil.write(out, bs);
			});
		}
		return flag == null ? false : flag;
	}

	public BPResource getResource()
	{
		return m_res;
	}

	public void close()
	{
	}

	public void open()
	{
	}

	public String getTitle()
	{
		return m_res.getName();
	}

	public boolean canOpen()
	{
		if (m_res == null)
			return false;
		if (m_res.isIO() && !((BPResourceIO) m_res).exists())
			return false;
		return true;
	}

	public <T> T useInputStream(Function<InputStream, T> in)
	{
		BPResource res = m_res;
		if (res.isIO())
		{
			return ((BPResourceIO) m_res).useInputStream(in);
		}
		return null;
	}

	public <T> T useOutputStream(Function<OutputStream, T> out)
	{
		BPResource res = m_res;
		if (res.isIO())
		{
			return ((BPResourceIO) m_res).useOutputStream(out);
		}
		return null;
	}
}
