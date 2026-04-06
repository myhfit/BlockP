package bp.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LockUtil
{
	public final static <R> R rwLock(ReadWriteLock rwlock, boolean iswritelock, Supplier<R> cb)
	{
		Lock l = iswritelock ? rwlock.writeLock() : rwlock.readLock();
		l.lock();
		try
		{
			return cb.get();
		}
		finally
		{
			l.unlock();
		}
	}

	public final static void rwLock(ReadWriteLock rwlock, boolean iswritelock, Runnable cb)
	{
		Lock l = iswritelock ? rwlock.writeLock() : rwlock.readLock();
		l.lock();
		try
		{
			cb.run();
		}
		finally
		{
			l.unlock();
		}
	}

	public final static <R> R lock(Lock l, Supplier<R> cb)
	{
		l.lock();
		try
		{
			return cb.get();
		}
		finally
		{
			l.unlock();
		}
	}

	public final static void lock(Lock l, Runnable cb)
	{
		l.lock();
		try
		{
			cb.run();
		}
		finally
		{
			l.unlock();
		}
	}

	public final static class BPLockedRef<T>
	{
		protected ReadWriteLock m_rwlock;
		protected volatile T m_value;

		public BPLockedRef()
		{
			m_rwlock = new ReentrantReadWriteLock();
		}

		public BPLockedRef(T value)
		{
			m_value = value;
		}

		public T getNoLock()
		{
			return m_value;
		}

		public T get()
		{
			return rwLock(m_rwlock, false, () -> m_value);
		}

		public T lazyInit(Supplier<T> initfunc)
		{
			return checkOrSet(v -> v != null, initfunc);
		}

		public T checkOrSet(Predicate<T> checkfunc, Supplier<T> initfunc)
		{
			T v = rwLock(m_rwlock, false, () -> checkfunc.test(m_value) ? m_value : null);
			if (v != null)
				return v;
			return rwLock(m_rwlock, true, () ->
			{
				m_value = initfunc.get();
				return m_value;
			});
		}
	}
}
