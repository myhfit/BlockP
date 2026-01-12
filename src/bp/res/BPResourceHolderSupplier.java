package bp.res;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.function.Supplier;

import bp.util.LogicUtil.WeakRefGo;

public class BPResourceHolderSupplier<R> extends BPResourceHolder
{
	protected volatile WeakRefGo<Supplier<R>> m_cbref;
	protected volatile boolean m_docache;

	public BPResourceHolderSupplier(Supplier<R> cb, boolean docache, BPResource parent, String ext, String id, String name, boolean isleaf)
	{
		super(new byte[0], parent, ext, id, name, isleaf);
		m_docache = docache;
		m_cbref = new WeakRefGo<Supplier<R>>(cb);
	}

	@SuppressWarnings("unchecked")
	public <T> T getData()
	{
		return (T) readFromSupplier();
	}

	@SuppressWarnings("unchecked")
	protected synchronized R readFromSupplier()
	{
		if (m_docache)
		{
			if (m_cbref != null)
			{
				R data = m_cbref.exec(f -> f.get());
				m_data = data;
				m_cbref = null;
				return data;
			}
			else
			{
				return (R) m_data;
			}
		}
		else
		{
			return m_cbref.exec(f -> f.get());
		}
	}

	public <T> T useInputStream(Function<InputStream, T> in)
	{
		return null;
	}

	public <T> T useOutputStream(Function<OutputStream, T> out)
	{
		return null;
	}

	public boolean exists()
	{
		return m_cbref != null;
	}

	public void release()
	{
		m_cbref = null;
		super.release();
	}

	public long getSize()
	{
		return 0;
	}
}
