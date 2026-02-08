package bp.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bp.data.reader.BPBytesReader;
import bp.data.reader.BPBytesReaderBB;
import bp.data.writer.BPBytesWriter;
import bp.data.writer.BPBytesWriterStream;

public class BPPDUtil
{
	public final static byte BT_NULL = 0;
	public final static byte BT_BOOL = 1;
	public final static byte BT_BYTE = 2;
	public final static byte BT_INT = 3;
	public final static byte BT_LONG = 4;
	public final static byte BT_FLOAT = 5;
	public final static byte BT_DOUBLE = 6;
	public final static byte BT_STR = 16;
	public final static byte BT_DATE = 17;
	public final static byte BT_LIST = 32;
	public final static byte BT_MAP = 48;

	public final static <T> T read(byte[] bs)
	{
		return read(new BPBytesReaderBB(bs));
	}

	@SuppressWarnings("unchecked")
	public final static <T> T read(BPBytesReader bb)
	{
		return (T) readObject(bb);
	}

	public static void write(OutputStream out, Object obj)
	{
		BPBytesWriter bw = new BPBytesWriterStream(out);
		writeObject(bw, obj);
		try
		{
			out.flush();
		}
		catch (IOException e)
		{
		}
	}

	protected final static Object readObject(BPBytesReader bb)
	{
		byte bt = bb.get();
		switch (bt)
		{
			case BT_NULL:
				return null;
			case BT_BOOL:
				return bb.get() != 0;
			case BT_BYTE:
				return bb.get();
			case BT_INT:
				return bb.getInt();
			case BT_LONG:
				return bb.getLong();
			case BT_FLOAT:
				return bb.getFloat();
			case BT_DOUBLE:
				return bb.getDouble();
			case BT_STR:
				return readStr(bb);
			case BT_DATE:
				return new Date(bb.getLong());
			case BT_LIST:
				return readList(bb);
			case BT_MAP:
				return readMap(bb);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected final static void writeObject(BPBytesWriter bw, Object v)
	{
		if (v == null)
		{
			bw.put(BT_NULL);
		}
		else
		{
			Class<?> c = v.getClass();
			if (c == Boolean.class || c == boolean.class)
			{
				bw.put(BT_BOOL);
				bw.put((byte) (((boolean) v) ? 1 : 0));
			}
			else if (c == Byte.class || c == byte.class)
			{
				bw.put(BT_BYTE);
				bw.put((byte) v);
			}
			else if (c == Integer.class || c == int.class)
			{
				bw.put(BT_INT);
				bw.putInt((int) v);
			}
			else if (c == Long.class || c == long.class)
			{
				bw.put(BT_LONG);
				bw.putLong((long) v);
			}
			else if (c == Float.class || c == float.class)
			{
				bw.put(BT_FLOAT);
				bw.putFloat((float) v);
			}
			else if (c == Double.class || c == double.class)
			{
				bw.put(BT_DOUBLE);
				bw.putDouble((double) v);
			}
			else if (c == String.class)
			{
				bw.put(BT_STR);
				byte[] bs = TextUtil.fromString((String) v, "utf-8");
				bw.putInt(bs.length);
				bw.put(bs);
			}
			else if (c == Date.class)
			{
				bw.put(BT_DATE);
				bw.putLong(((Date) v).getTime());
			}
			else if (v instanceof Collection)
			{
				bw.put(BT_LIST);
				writeList(bw, (Collection<?>) v);
			}
			else if (v instanceof Map)
			{
				bw.put(BT_MAP);
				writeMap(bw, (Map<String, ?>) v);
			}
		}
	}

	private final static String readStr(BPBytesReader bb)
	{
		long len = bb.getInt();
		byte[] bs = new byte[(int) len];
		bb.get(bs);
		return TextUtil.toString(bs, "utf-8");
	}

	private final static List<Object> readList(BPBytesReader bb)
	{
		int c = 0;
		int s = bb.getInt();
		List<Object> rc = new ArrayList<Object>();
		while (c < s)
		{
			Object o = readObject(bb);
			rc.add(o);
			c++;
		}
		return rc;
	}

	private final static void writeList(BPBytesWriter bw, Collection<?> l)
	{
		bw.putInt(l.size());
		for (Object v : l)
		{
			writeObject(bw, v);
		}
	}

	private final static Map<String, Object> readMap(BPBytesReader bb)
	{
		int c = 0;
		int s = bb.getInt();
		Map<String, Object> rc = new LinkedHashMap<String, Object>();
		while (c < s)
		{
			String k = readStr(bb);
			Object v = readObject(bb);
			rc.put(k, v);
			c++;
		}
		return rc;
	}

	private final static void writeMap(BPBytesWriter bw, Map<String, ?> m)
	{
		bw.putInt(m.size());
		for (String k : m.keySet())
		{
			byte[] bs = TextUtil.fromString(k, "utf-8");
			bw.putInt(bs.length);
			bw.put(bs);
			writeObject(bw, m.get(k));
		}
	}
}
