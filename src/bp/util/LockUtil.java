package bp.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
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
}
