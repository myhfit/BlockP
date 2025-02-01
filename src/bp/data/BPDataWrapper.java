package bp.data;

import java.lang.ref.WeakReference;

public class BPDataWrapper<T>
{
	protected volatile Ref<T> m_ref;

	public BPDataWrapper(T data)
	{
		set(data, false);
	}

	public void set(T data)
	{
		set(data, false);
	}

	public void set(T data, boolean weak)
	{
		if (weak)
		{
			m_ref = new WeakRef<T>(data);
		}
		else
		{
			m_ref = new STRef<T>(data);
		}
	}

	public T get()
	{
		Ref<T> ref = m_ref;
		return ref == null ? null : ref.get();
	}

	public void clear()
	{
		m_ref = null;
	}

	protected static interface Ref<T>
	{
		T get();
	}

	protected final static class WeakRef<T> extends WeakReference<T> implements Ref<T>
	{
		public WeakRef(T data)
		{
			super(data);
		}
	}

	protected final static class STRef<T> implements Ref<T>
	{
		private volatile T m_data;

		public STRef(T data)
		{
			set(data);
		}

		public T get()
		{
			return m_data;
		}

		public void set(T data)
		{
			m_data = data;
		}

	}
}
