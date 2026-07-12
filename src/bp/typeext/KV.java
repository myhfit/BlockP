package bp.typeext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class KV implements Pair<String, Object>
{
	public String key;
	public Object value;

	public KV()
	{
		super();
	}

	public KV(String k, Object v)
	{
		key = k;
		value = v;
	}

	public String getLeft()
	{
		return key;
	}

	public Object getRight()
	{
		return value;
	}

	public String toString()
	{
		return (key != null ? key.toString() : "") + ":" + (value != null ? value.toString() : "");
	}

	public static List<KV> getKVs(Map<String, Object> data)
	{
		return new KVs(data);
	}

	public static class KVs extends ArrayList<KV>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -212827699928319278L;

		public KVs(Map<String, Object> data)
		{
			for (Entry<String, Object> entry : data.entrySet())
			{
				KV kv = new KV();
				kv.key = entry.getKey();
				kv.value = entry.getValue();
				add(kv);
			}
		}
	}
}