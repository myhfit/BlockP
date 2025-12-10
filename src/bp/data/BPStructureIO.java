package bp.data;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bp.data.reader.BPBytesReader;
import bp.data.reader.BPBytesReaderBB;
import bp.util.ClassUtil;
import bp.util.IOUtil;
import bp.util.Std;

public interface BPStructureIO
{
	default <T> T read(InputStream in) throws Exception
	{
		return read(in, null);
	}

	default <T> T read(InputStream in, BPStructureIOContext context) throws Exception
	{
		byte[] bs = IOUtil.read(in);
		BPBytesReader reader=new BPBytesReaderBB(bs);
		initContext(reader, context);
		return read(reader, context);
	}

	default <T> T read(BPBytesReader reader) throws Exception
	{
		return read(reader, null);
	}

	@SuppressWarnings("unchecked")
	default <T> T read(BPBytesReader r, BPStructureIOContext context) throws Exception
	{
		if (context == null)
			context = new BPStructureIOContext();
		initContext(r, context);
		BPStructureIO thiso = predicate(r, context);
		thiso.readDatas(r, context);
		return (T) thiso;
	}

	default void initContext(BPBytesReader reader, BPStructureIOContext context)
	{

	}

	@SuppressWarnings("unchecked")
	default <T extends BPStructureIO> T predicate(BPBytesReader reader, BPStructureIOContext context) throws Exception
	{
		return (T) this;
	}

	default void readDatas(BPBytesReader reader, BPStructureIOContext context) throws Exception
	{
		context.objstack.add(this);
		context.lastpos = reader.position();
		Class<?> c = getClass();
		List<Field> fs = context.getFields(c);
		boolean dealed = false;
		boolean needstop = false;
		FieldOption fo = null;
		Map<String, Field> fmap = new TreeMap<String, Field>();
		Method mv;
		for (Field f : fs)
		{
			fmap.put(f.getName(), f);
			Annotation[] annos = f.getAnnotations();
			dealed = false;
			needstop = false;
			fo = null;
			mv = null;
			for (Annotation anno : annos)
			{
				if (anno instanceof FieldOption)
				{
					fo = (FieldOption) anno;
					if (fo.ignoreRead())
					{
						dealed = true;
					}
					else
					{
						{
							String fm = fo.handleReadMethod();
							if (fm != null && fm.length() > 0)
							{
								Method m2 = context.getMethod(c, fm, BPStructureIOMethods.READMETHOD);
								f.set(this, m2.invoke(this, reader, context));
								dealed = true;
							}
						}
						{
							String fmv = fo.handleReadValueMethod();
							if (fmv != null && fmv.length() > 0)
							{
								mv = context.getMethod(c, fmv, BPStructureIOMethods.READMETHOD);
							}
						}
					}
					if (fo.stopRead())
					{
						needstop = true;
					}

					break;
				}
			}
			if (!dealed)
			{
				readField(reader, context, f, fo, fmap, mv);
			}
			if (needstop)
				break;
		}
		context.lastpos = reader.position();
		readFinished(context);
		context.objstack.removeLast();
	}

	default Object readFieldValue(BPBytesReader reader, BPStructureIOContext context, Class<?> c, FieldOption fo, int len, Method m) throws Exception
	{
		if (m != null)
			return m.invoke(this, reader, context);
		if (byte.class == c)
			return reader.get();
		else if (short.class == c)
			return reader.getShort();
		else if (int.class == c)
			return reader.getInt();
		else if (long.class == c)
			return reader.getLong();
		else if (float.class == c)
			return reader.getFloat();
		else if (double.class == c)
			return reader.getDouble();
		else if (len != 0)
			reader.position(reader.position() + len);
		else if (BPStructureIO.class.isAssignableFrom(c))
		{
			try
			{
				BPStructureIO newdata = (BPStructureIO) c.newInstance();
				newdata.read(reader, context);
				return newdata;
			}
			catch (Exception e)
			{
				Std.debug(e.getMessage());
			}
		}
		return null;
	}

	default void readField(BPBytesReader reader, BPStructureIOContext context, Field f, FieldOption fo, Map<String, Field> fmap, Method m) throws Exception
	{
		// Std.debug(reader.position()+"");
		Class<?> c = f.getType();
		if (c.isArray())
		{
			int l = fo.arrLength();
			if (l < 0)
				l = ((Number) fmap.get(fo.arrLengthField()).get(this)).intValue();
			Class<?> cc = c.getComponentType();
			Object arr = null;
			if (l >= 0)
			{
				arr = Array.newInstance(cc, l);
				for (int i = 0; i < l; i++)
					Array.set(arr, i, readFieldValue(reader, context, cc, fo, 0, m));
			}
			f.set(this, arr);
		}
		else
		{
			f.set(this, readFieldValue(reader, context, c, fo, 0, m));
		}
	}

	default void readFinished(BPStructureIOContext context)
	{

	}

	default String readCStr(BPBytesReader reader)
	{
		byte[] bs = new byte[1024];
		byte b = reader.get();
		int i = 0;
		while (b != 0)
		{
			bs[i++] = b;
			b = reader.get();
		}
		try
		{
			return new String(bs, 0, i, "utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			return null;
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface FieldOption
	{
		boolean ignoreRead() default false;

		boolean stopRead() default false;

		String handleReadMethod() default "";

		String handleReadValueMethod() default "";

		int arrLength() default -1;

		String arrLengthField() default "";
	}

	public static enum BPStructureIOMethods
	{
		READMETHOD(new Class[] { BPBytesReader.class, BPStructureIOContext.class });

		protected Class<?>[] m_ptypes;

		BPStructureIOMethods(Class<?>[] ptypes)
		{
			m_ptypes = ptypes;
		}

		public Class<?>[] getParameterTypes()
		{
			return m_ptypes;
		}
	}

	public static class BPStructureIOContext
	{
		public LinkedList<Object> objstack;
		public boolean ignoreerr;
		public int lastpos;

		protected Map<Class<?>, List<Field>> fscache;
		protected Map<Class<?>, Map<String, Method>> mscache;

		public BPStructureIOContext()
		{
			objstack = new LinkedList<Object>();
			fscache = new HashMap<Class<?>, List<Field>>();
			mscache = new HashMap<Class<?>, Map<String, Method>>();
		}

		public List<Field> getFields(Class<?> c)
		{
			List<Field> rc = fscache.get(c);
			if (rc == null)
			{
				rc = ClassUtil.getFields(c);
				fscache.put(c, rc);
			}
			return rc;
		}

		public Method getMethod(Class<?> c, String mname, BPStructureIOMethods op)
		{
			Map<String, Method> ms = mscache.get(c);
			Method rc = null;
			String k = mname + "," + op;
			if (ms != null)
			{
				if (ms.containsKey(k))
					rc = ms.get(k);
				else
				{
					try
					{
						rc = c.getMethod(mname, op.getParameterTypes());
					}
					catch (NoSuchMethodException | SecurityException e)
					{
						Std.err(e);
					}
					ms.put(k, rc);
				}
			}
			else
			{
				ms = new HashMap<String, Method>();
				mscache.put(c, ms);
				try
				{
					rc = c.getMethod(mname, op.getParameterTypes());
				}
				catch (NoSuchMethodException | SecurityException e)
				{
					Std.err(e);
				}
				ms.put(k, rc);
			}
			return rc;
		}
	}
}
