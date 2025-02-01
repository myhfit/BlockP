package bp.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import bp.data.BPMData;
import bp.data.BPSLData;

public class ObjUtil
{
	public final static Map<String, Object> getMappedData(Object obj)
	{
		Map<String, Object> rc = null;
		if (obj instanceof BPMData)
		{
			return ((BPMData) obj).getMappedData();
		}
		else
		{
			rc = new HashMap<String, Object>();
		}
		return rc;
	}

	public final static String toString(Object v)
	{
		return toString(v, null);
	}

	public final static String toString(Object v, String defaulttext)
	{
		if (v == null)
			return defaulttext;
		if (v instanceof Number)
		{
			if (v instanceof Long)
				return Long.toString((long) v);
			if (v instanceof Double)
				return Double.toString((double) v);
			if (v instanceof Float)
				return Float.toString((float) v);
		}
		else if (v instanceof Date)
		{
			return v.toString();
		}
		return v.toString();
	}

	public static boolean check(RunnableB seg)
	{
		try
		{
			seg.run();
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static interface RunnableB
	{
		void run() throws Exception;
	}

	@SuppressWarnings("unchecked")
	public final static <T> T castObject(Object v, Class<T> cls, T defaultvalue)
	{
		if (v == null)
			return null;
		if (cls == String.class)
		{
			return (T) toString(v, (String) defaultvalue);
		}
		else if (cls == Integer.class)
		{
			return (T) toInt(v, (Integer) defaultvalue);
		}
		else if (cls == Long.class)
		{
			return (T) toLong(v, (Long) defaultvalue);
		}
		else if (cls == Byte.class)
		{
			return (T) toByte(v, (Byte) defaultvalue);
		}
		else if (cls == Short.class)
		{
			return (T) toShort(v, (Short) defaultvalue);
		}
		return cls.cast(v);
	}

	public final static Integer toInt(Object v, int defaultvalue)
	{
		return toInt(v, (Integer) defaultvalue);
	}

	public final static Integer toInt(Object v, Integer defaultvalue)
	{
		Integer value = defaultvalue;
		if (v == null)
		{

		}
		else if (v instanceof Number)
		{
			value = ((Number) v).intValue();
		}
		else
		{
			try
			{
				value = Integer.parseInt(v.toString());
			}
			catch (NumberFormatException e)
			{
			}
		}
		return value;
	}

	public final static Long toLong(Object v, long defaultvalue)
	{
		return toLong(v, (Long) defaultvalue);
	}

	public final static Long toLong(Object v, Long defaultvalue)
	{
		Long value = defaultvalue;
		if (v == null)
		{

		}
		else if (v instanceof Number)
		{
			value = ((Number) v).longValue();
		}
		else
		{
			try
			{
				value = Long.parseLong(v.toString());
			}
			catch (NumberFormatException e)
			{
				Std.err(e);
			}
		}
		return value;
	}

	public final static Double toDouble(Object v, double defaultvalue)
	{
		return toDouble(v, (Double) defaultvalue);
	}

	public final static Double toDouble(Object v, Double defaultvalue)
	{
		Double value = defaultvalue;
		if (v == null)
		{

		}
		else if (v instanceof Number)
		{
			value = ((Number) v).doubleValue();
		}
		else
		{
			try
			{
				value = Double.parseDouble(v.toString());
			}
			catch (NumberFormatException e)
			{
				Std.err(e);
			}
		}
		return value;
	}

	public final static Float toFloat(Object v, float defaultvalue)
	{
		return toFloat(v, (Float) defaultvalue);
	}

	public final static Float toFloat(Object v, Float defaultvalue)
	{
		Float value = defaultvalue;
		if (v == null)
		{

		}
		else if (v instanceof Number)
		{
			value = ((Number) v).floatValue();
		}
		else
		{
			try
			{
				value = Float.parseFloat(v.toString());
			}
			catch (NumberFormatException e)
			{
				Std.err(e);
			}
		}
		return value;
	}

	public final static Byte toByte(Object v, Byte defaultvalue)
	{
		Byte value = defaultvalue;
		if (v == null)
		{

		}
		else if (v instanceof Number)
		{
			value = ((Number) v).byteValue();
		}
		else
		{
			try
			{
				value = Byte.parseByte(v.toString());
			}
			catch (NumberFormatException e)
			{
			}
		}
		return value;
	}

	public final static Short toShort(Object v, Short defaultvalue)
	{
		Short value = defaultvalue;
		if (v == null)
		{

		}
		else if (v instanceof Number)
		{
			value = ((Number) v).shortValue();
		}
		else
		{
			try
			{
				value = Short.parseShort(v.toString());
			}
			catch (NumberFormatException e)
			{
			}
		}
		return value;
	}

	public final static <T> Map<String, String> toPlainMap(List<T> datas, Function<T, String[]> transfunc)
	{
		Map<String, String> rc = null;
		if (datas != null)
		{
			rc = new HashMap<String, String>();
			for (T t : datas)
			{
				String[] kv = transfunc.apply(t);
				rc.put(kv[0], kv[1]);
			}
		}
		return rc;
	}

	public final static Boolean toBool(Object v, Boolean defaultvalue)
	{
		Boolean rc = defaultvalue;
		if (v != null)
		{
			if (v instanceof Boolean)
			{
				return (Boolean) v;
			}
			else
			{
				return v.toString().equalsIgnoreCase("true");
			}
		}
		return rc;
	}

	public final static Map<String, String> toPlainMap(Map<String, ?> map)
	{
		return toPlainMap(map, false);
	}

	public final static Map<String, String> toPlainMap(Map<String, ?> map, boolean needorder)
	{
		HashMap<String, String> rc = needorder ? new LinkedHashMap<String, String>() : new HashMap<String, String>();
		for (Entry<String, ?> entry : map.entrySet())
		{
			rc.put(entry.getKey(), toString(entry.getValue(), ""));
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	public final static <T> T mapToObj(Map<String, Object> map)
	{
		if (map == null)
			return null;
		for (Entry<String, Object> entry : map.entrySet())
		{
			Object v = entry.getValue();
			if (v != null)
			{
				if (v instanceof List)
				{
					listToObj((List<?>) v);
				}
				else if (v instanceof Map)
				{
					Object newo = mapToObj((Map<String, Object>) v);
					if (newo != v)
					{
						entry.setValue(newo);
					}
				}
			}
		}

		String clsname = (String) map.get(BPSLData.CLSNAME_FIELD);
		if (clsname != null)
		{
			Object obj = ClassUtil.createObject(clsname);
			((BPSLData) obj).setLoadData(map);
			return (T) obj;
		}
		else
		{
			return (T) map;
		}
	}

	@SuppressWarnings("unchecked")
	public final static <T> T mapToObj2(Map<String, ?> map, boolean loadsub)
	{
		if (map == null)
			return null;
		Map<String, Object> m2 = new HashMap<String, Object>();
		for (Entry<String, ?> entry : map.entrySet())
		{
			Object v = entry.getValue();
			if (loadsub && v != null)
			{
				if (v instanceof List)
				{
					v = listToObj2((List<?>) v, true);
				}
				else if (v instanceof Map)
				{
					v = mapToObj2((Map<String, Object>) v, true);
				}
			}
			m2.put(entry.getKey(), v);
		}

		String clsname = (String) map.get(BPSLData.CLSNAME_FIELD);
		if (clsname != null)
		{
			Object obj = ClassUtil.createObject(clsname);
			((BPSLData) obj).setLoadData(m2);
			return (T) obj;
		}
		else
		{
			return (T) m2;
		}
	}

	@SuppressWarnings("unchecked")
	public final static Map<String, Object> objToMap(Object obj)
	{
		if (obj == null)
			return null;
		if (obj instanceof BPSLData)
		{
			return ((BPSLData) obj).getSaveData();
		}
		else if (obj instanceof Map)
		{
			return new HashMap<String, Object>((Map<String, Object>) obj);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final static void listToObj(List<?> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			Object o = list.get(i);
			if (o != null)
			{
				if (o instanceof List)
				{
					listToObj((List<?>) o);
				}
				else if (o instanceof Map)
				{
					Object newo = mapToObj((Map<String, Object>) o);
					if (newo != o)
					{
						((List) list).set(i, newo);
					}
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	public final static <T> List<T> listToObj2(List<?> list, boolean loadsub)
	{
		if (list == null)
			return null;
		List<T> rc = new ArrayList<T>();
		for (int i = 0; i < list.size(); i++)
		{
			Object o = list.get(i);
			T li = null;
			if (loadsub && o != null)
			{
				if (o instanceof List)
				{
					li = (T) listToObj2((List<?>) o, true);
				}
				else if (o instanceof Map)
				{
					li = mapToObj2((Map<String, ?>) o, true);
				}
				else
				{
					li = (T) o;
				}
			}
			rc.add(li);
		}
		return rc;
	}

	public final static <T> T extract(Object m, String key)
	{
		return extract(m, key.split("\\."), 0);
	}

	@SuppressWarnings("unchecked")
	private final static <T> T extract(Object obj, String[] keys, int index)
	{
		String key = keys[index];
		T rc = null;
		Object v = null;
		if (obj instanceof Map)
		{
			Map<String, Object> m = (Map<String, Object>) obj;
			v = m.get(key);
		}
		else if (obj instanceof List)
		{
			List<Object> l = (List<Object>) obj;
			v = l.get(Integer.parseInt(key));
		}
		if (index >= keys.length - 1)
		{
			rc = (T) v;
		}
		else if (v != null)
		{
			rc = extract(v, keys, index + 1);
		}
		return rc;
	}

	public final static Map<String, Object> makeMap(Object... ps)
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		for (int i = 0; i < ps.length; i += 2)
		{
			rc.put((String) ps[i], ps[i + 1]);
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	public final static <T> List<T> makeList(Object... ps)
	{
		List<T> rc = new ArrayList<T>();
		for (Object p : ps)
			rc.add((T) p);
		return rc;
	}

	public final static <T extends Enum<T>> T enumFromOrdinal(Class<T> eclass, int v)
	{
		for (T t : eclass.getEnumConstants())
		{
			if (t.ordinal() == v)
				return t;
		}
		return null;
	}

	public final static class Wrapper<T>
	{
		public T data;

		public Wrapper()
		{

		}

		public Wrapper(T data)
		{
			this.data = data;
		}
	}
}
