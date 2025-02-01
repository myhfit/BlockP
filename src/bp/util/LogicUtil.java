package bp.util;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
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

	@SuppressWarnings("unchecked")
	public final static <R> R IFV_M(Map<String, ?> m, String key)
	{
		if (m == null)
			return null;
		return (R) m.get(key);
	}

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

		public <V> V exec(Function<T, V> seg)
		{
			T target = m_ref.get();
			if (target != null)
				return seg.apply(target);
			return null;
		}

		public void callRunnable()
		{
			Runnable cb = (Runnable) m_ref.get();
			if (cb != null)
				cb.run();
		}

		public T get()
		{
			return m_ref.get();
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
				data = cls.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
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
}
