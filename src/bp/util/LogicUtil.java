package bp.util;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LogicUtil
{
	// if v not empty use it do something
	public final static <V> void IFVU(V v, Consumer<V> con)
	{
		if (v != null)
		{
			con.accept(v);
		}
	}

	// if v not empty use it do something
	public final static <V> void IFVU_REF(WeakReference<V> vref, Consumer<V> con)
	{
		V v = vref.get();
		if (v != null)
		{
			con.accept(v);
		}
	}

	// if v not empty use it do something and return
	public final static <V, R> R IFVR(V v, Function<V, R> func)
	{
		return v != null ? func.apply(v) : null;
	}

	// if m not empty get key from map
	@SuppressWarnings("unchecked")
	public final static <R> R IFV_M(Map<String, ?> m, String key)
	{
		if (m == null)
			return null;
		return (R) m.get(key);
	}

	// chained apply function
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final static <T> T CHAIN_NN(Object raw, Function<?, ?>... fs)
	{
		Object r = raw;
		for (Function<?, ?> f : fs)
		{
			if (r == null)
				break;
			r = ((Function) f).apply(r);
		}
		return (T) r;
	}

	public final static <T> ChainBuilder<T> buildChain(T t)
	{
		return new ChainBuilder<T>(t);
	}

	public final static class ChainBuilder<T>
	{
		public T target;

		public ChainBuilder(T t)
		{
			target = t;
		}

		public <R> ChainBuilder<R> chain(Function<T, R> func)
		{
			R v = null;
			if (target != null)
				v = func.apply(target);
			return new ChainBuilder<>(v);
		}
		
		public <R> ChainBuilder<R> chain(Supplier<R> func)
		{
			R v = null;
			if (target != null)
				v = func.get();
			return new ChainBuilder<>(v);
		}
	}

	// parallel apply function,return when not null
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final static <T> T PAR_NN(Object raw, Function<?, ?>... fs)
	{
		Object r = null;
		for (Function<?, ?> f : fs)
		{
			r = ((Function) f).apply(raw);
			if (r != null)
				break;
		}
		return (T) r;
	}

	public final static <T> T NVL(T v, T defaultvalue)
	{
		return v != null ? v : defaultvalue;
	}

	// value test and link to second value
	public final static <T> T VLS(T v, Predicate<T> checkfunc, T targetvalue)
	{
		return checkfunc.test(v) ? targetvalue : v;
	}

	// value test and link to function
	public final static <T> void VLF(T v, Predicate<T> checkfunc, Consumer<T> targetfunc)
	{
		if (checkfunc.test(v))
			targetfunc.accept(v);
	}
	
	// compact if to one line
	public final static void IFC(boolean v, Runnable segtrue, Runnable segfalse)
	{
		if (v)
		{
			if (segtrue != null)
				segtrue.run();
		}
		else
		{
			if (segfalse != null)
				segfalse.run();
		}
	}

	public final static String tryGetNotEmptyStr(Object obj)
	{
		if (obj == null)
			return (String) obj;
		String str = (String) obj;
		return str.length() == 0 ? null : str;
	}

	public final static Supplier<Boolean> andCheck(Supplier<Boolean> check, Supplier<Boolean> check2)
	{
		if (check == null)
			return check2;
		return new CombineChecker(check, check2, CombineChecker.OP_AND);
	}
	
	public final static <T> T unwrapReference(Reference<T> ref)
	{
		return ref != null ? ref.get() : null;
	}

	public final static class CombineChecker implements Supplier<Boolean>
	{
		public final static int OP_AND = 1;
		public final static int OP_OR = 2;
		public final static int OP_XOR = 3;

		private int op;
		private Supplier<Boolean> s1;
		private Supplier<Boolean> s2;

		public CombineChecker(Supplier<Boolean> s1, Supplier<Boolean> s2, int op)
		{
			this.s1 = s1;
			this.s2 = s2;
			this.op = op;
		}

		public Boolean get()
		{
			switch (op)
			{
				case OP_AND:
					return s1.get() && s2.get();
				case OP_OR:
					return s1.get() || s2.get();
				case OP_XOR:
					return s1.get().booleanValue() != s2.get().booleanValue();
			}
			return false;
		}
	}

	public static class WeakRefGo<T>
	{
		protected volatile WeakReference<T> m_ref;

		public WeakRefGo(T target)
		{
			setTarget(target);
		}

		public WeakRefGo()
		{
			this(null);
		}

		public void setTarget(T target)
		{
			m_ref = new WeakReference<T>(target);
		}

		public void run(Consumer<T> seg)
		{
			T target = m_ref.get();
			if (target != null)
				seg.accept(target);
		}

		public <P> void run(BiConsumer<T, P> seg, P params)
		{
			T target = m_ref.get();
			if (target != null)
				seg.accept(target, params);
		}

		public void runDynamic(Class<?> cls, String method, Object... params)
		{
			T target = m_ref.get();
			if (target != null)
			{
				ClassUtil.tryCallSimpleMethod(cls, method, target, params);
			}
		}

		public <V> V exec(Function<T, V> seg)
		{
			T target = m_ref.get();
			if (target != null)
				return seg.apply(target);
			return null;
		}

		public <V> V execDynamic(Class<?> cls, String method, Object... params)
		{
			T target = m_ref.get();
			if (target != null)
				ClassUtil.tryCallSimpleMethod(cls, method, target, params);
			return null;
		}

		public <V, ERR extends Exception> V execWithErr(EFunction2<T, V, ERR> seg) throws ERR
		{
			T target = m_ref.get();
			if (target != null)
			{
				try
				{
					return seg.apply(target);
				}
				catch (Exception e)
				{
					throw e;
				}
			}
			return null;
		}

		public void callRunnable()
		{
			Runnable cb = (Runnable) m_ref.get();
			if (cb != null)
				cb.run();
		}

		public <ERR extends Exception> void acceptWithErr(EConsumer2<T, ERR> seg) throws ERR
		{
			T target = m_ref.get();
			if (target != null)
			{
				try
				{
					seg.accept(target);
				}
				catch (Exception e)
				{
					throw e;
				}
			}
		}

		public T get()
		{
			return m_ref.get();
		}

		@SuppressWarnings("unchecked")
		public T getIFCProxy(Class<?> ifcclass)
		{
			return (T) Proxy.newProxyInstance(ifcclass.getClassLoader(), new Class[] { ifcclass }, this::invoke);
		}

		protected Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			T t = m_ref.get();
			if (t == null)
				return null;
			else
				return method.invoke(t, args);
		}
	}

	public static class WeakRefGoPredicate<T> extends WeakRefGo<Predicate<T>>
	{
		public WeakRefGoPredicate(Predicate<T> target)
		{
			super(target);
		}

		public WeakRefGoPredicate()
		{
			this(null);
		}

		public Boolean test(T value)
		{
			Predicate<T> cb = m_ref.get();
			if (cb != null)
				return cb.test(value);
			return null;
		}

		public boolean test(T value, boolean dv)
		{
			Predicate<T> cb = m_ref.get();
			if (cb != null)
				return cb.test(value);
			return dv;
		}
	}

	public static class WeakRefGoConsumer<T> extends WeakRefGo<Consumer<T>>
	{
		public WeakRefGoConsumer(Consumer<T> target)
		{
			super(target);
		}

		public WeakRefGoConsumer()
		{
			this(null);
		}

		public void accept(T value)
		{
			Consumer<T> cb = m_ref.get();
			if (cb != null)
				cb.accept(value);
		}
	}

	public static class WeakRefGoBiConsumer<T, U> extends WeakRefGo<BiConsumer<T, U>>
	{
		public WeakRefGoBiConsumer(BiConsumer<T, U> target)
		{
			super(target);
		}

		public WeakRefGoBiConsumer()
		{
			this(null);
		}

		public void accept(T v, U u)
		{
			BiConsumer<T, U> cb = m_ref.get();
			if (cb != null)
				cb.accept(v, u);
		}
	}

	public static class WeakRefGoFunction<T, V> extends WeakRefGo<Function<T, V>>
	{
		public WeakRefGoFunction(Function<T, V> target)
		{
			super(target);
		}

		public WeakRefGoFunction()
		{
			this(null);
		}

		public V apply(T value)
		{
			Function<T, V> cb = m_ref.get();
			if (cb != null)
				return cb.apply(value);
			return null;
		}
	}

	public static class WeakRefGoBiFunction<T, U, R> extends WeakRefGo<BiFunction<T, U, R>>
	{
		public WeakRefGoBiFunction(BiFunction<T, U, R> target)
		{
			super(target);
		}

		public WeakRefGoBiFunction()
		{
			this(null);
		}

		public R apply(T t, U u)
		{
			BiFunction<T, U, R> cb = m_ref.get();
			if (cb != null)
				return cb.apply(t, u);
			return null;
		}
	}

	public static final class Builder<T>
	{
		private T data;

		private Builder(T t)
		{
			data = t;
		}

		public static <T> Builder<T> byCreate(Class<T> cls)
		{
			T data = null;
			try
			{
				data = cls.getConstructor().newInstance();
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
			{
				Std.err(e);
			}
			return new Builder<T>(data);
		}

		public static <T> Builder<T> bySet(T t)
		{
			return new Builder<T>(t);
		}

		public T get()
		{
			return data;
		}

		public Builder<T> with(Consumer<T> cb)
		{
			cb.accept(data);
			return this;
		}
	}

	@SuppressWarnings("unchecked")
	public final static <T extends Throwable> T catchRun(ERunnable2<T> seg, Class<T> errclass) throws Throwable
	{
		try
		{
			seg.run();
		}
		catch (Exception e)
		{
			if (errclass != null)
			{
				if (errclass.isInstance(e))
					return (T) e;
				else
					throw e;
			}
			else
				return (T) e;
		}
		return null;
	}

	public final static <ERR extends Throwable, R> R catchGet(ESupplier2<R, ERR> seg, R defaultvalue)
	{
		try
		{
			return seg.get();
		}
		catch (Throwable e)
		{
			return defaultvalue;
		}
	}

	public static interface ERunnable2<T extends Throwable>
	{
		void run() throws T;
	}

	public static interface ESupplier2<R, ERR extends Throwable>
	{
		R get() throws ERR;
	}

	public static interface EConsumer2<T, ERR extends Throwable>
	{
		void accept(T target) throws ERR;
	}

	public static interface EFunction2<T, R, ERR extends Throwable>
	{
		R apply(T t) throws ERR;
	}
}
