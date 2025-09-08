package bp.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import bp.res.BPResource;

public abstract class BPDataContainerOverlay<SRC extends BPDataContainer> implements BPDataContainer
{
	protected SRC m_src;

	public void setSource(SRC src)
	{
		m_src = src;
	}

	public SRC getSource()
	{
		return m_src;
	}

	public void close()
	{
		m_src.close();
	}

	public void open()
	{
		m_src.open();
	}

	public void bind(BPResource res)
	{
		m_src.bind(res);
	}

	public void unbind()
	{
		m_src.unbind();
	}

	public BPResource getResource()
	{
		return m_src.getResource();
	}

	public byte[] readAll()
	{
		return m_src.readAll();
	}

	public boolean writeAll(byte[] bs)
	{
		return m_src.writeAll(bs);
	}

	public abstract void initOverlay();

	public abstract void saveOverlay();

	public abstract void clearOverlay();

	public <T> T useInputStream(Function<InputStream, T> in)
	{
		return m_src.useInputStream(in);
	}

	public <T> T useOutputStream(Function<OutputStream, T> out)
	{
		return m_src.useOutputStream(out);
	}

	public String getTitle()
	{
		return m_src.getTitle();
	}

	public void writeOverlayToSource()
	{
		saveOverlay();
		clearOverlay();
		initOverlay();
	}

	public boolean isOverlay()
	{
		return true;
	}
}
