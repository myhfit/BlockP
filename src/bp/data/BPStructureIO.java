package bp.data;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bp.util.ClassUtil;
import bp.util.IOUtil;

public interface BPStructureIO
{
	default void read(InputStream in) throws Exception
	{
		byte[] bs = IOUtil.read(in);
		ByteBuffer bb = ByteBuffer.wrap(bs);
		read(bb);
	}

	default void read(ByteBuffer bb) throws Exception
	{
		Class<?> c = getClass();
		List<Field> fs = ClassUtil.getFields(c);
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
								Method m2 = c.getMethod(fm, new Class[] { ByteBuffer.class });
								f.set(this, m2.invoke(this, bb));
								dealed = true;
							}
						}
						{
							String fmv = fo.handleReadValueMethod();
							if (fmv != null && fmv.length() > 0)
							{
								mv = c.getMethod(fmv, new Class[] { ByteBuffer.class });
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
			if (needstop)
				break;
			if (!dealed)
			{
				readField(bb, f, fo, fmap, mv);
			}
		}
		readFinished();
	}

	default Object readFieldValue(ByteBuffer bb, Class<?> c, FieldOption fo, int len, Method m) throws Exception
	{
		if (m != null)
			return m.invoke(this, bb);
		if (byte.class == c)
			return bb.get();
		else if (short.class == c)
			return bb.getShort();
		else if (int.class == c)
			return bb.getInt();
		else if (long.class == c)
			return bb.getLong();
		else if (float.class == c)
			return bb.getFloat();
		else if (double.class == c)
			return bb.getDouble();
		else if (len != 0)
			bb.position(bb.position() + len);
		return null;
	}

	default void readField(ByteBuffer bb, Field f, FieldOption fo, Map<String, Field> fmap, Method m) throws Exception
	{
		Class<?> c = f.getType();
		if (c.isArray())
		{
			int l = ((Number) fmap.get(fo.arrLengthField()).get(this)).intValue();
			Class<?> cc = c.getComponentType();
			Object arr = Array.newInstance(cc, l);
			for (int i = 0; i < l; i++)
				Array.set(arr, i, readFieldValue(bb, cc, fo, 0, m));
			f.set(this, arr);
		}
		else
		{
			f.set(this, readFieldValue(bb, c, fo, 0, m));
		}
	}

	default void readFinished()
	{

	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface FieldOption
	{
		boolean ignoreRead() default false;

		boolean stopRead() default false;

		String handleReadMethod() default "";

		String handleReadValueMethod() default "";

		String arrLengthField() default "";
	}
}
