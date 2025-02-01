package bp.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import bp.config.BPConfig;
import bp.config.BPConfigSimple;
import bp.config.BPSetting;
import bp.util.LogicUtil;
import bp.util.ObjUtil;

public interface BPDataConsumer<T> extends Consumer<T>, BPSLData
{
	default void runSegment(Runnable seg)
	{
		setup();
		try
		{
			seg.run();
			finish();
		}
		finally
		{
			clear();
		}
	}

	default boolean isEndpoint()
	{
		return false;
	}

	default boolean isTransformer()
	{
		return false;
	}

	default void setup()
	{

	}

	default void finish()
	{

	}

	default void clear()
	{

	}

	String getInfo();

	void setID(String id);

	String getID();

	default BPSetting getSetting()
	{
		return null;
	}

	default void setSetting(BPConfig config)
	{
	}

	default Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		LogicUtil.IFVU(getSetting(), s -> rc.put("setting", s.getMappedData()));
		LogicUtil.IFVU(getID(), id -> rc.put("id", id));
		return rc;
	}

	@SuppressWarnings("unchecked")
	default void setMappedData(Map<String, Object> data)
	{
		LogicUtil.IFVU(data.get("setting"), smo -> setSetting(BPConfigSimple.fromData((Map<String, Object>) smo)));
		LogicUtil.IFVU(data.get("id"), id -> setID(ObjUtil.toString(id)));
	}

	public static abstract class BPDataConsumerBase<T> implements BPDataConsumer<T>
	{
		protected String m_id;

		public void setID(String id)
		{
			m_id = id;
		}

		public String getID()
		{
			return m_id;
		}
	}

	public static class BPDataConsumerCollector<T> extends BPDataConsumerBase<T>
	{
		protected List<T> m_datas;

		public BPDataConsumerCollector()
		{
		}

		public void accept(T t)
		{
			m_datas.add(t);
		}

		public List<T> getDatas()
		{
			return m_datas;
		}

		public String getInfo()
		{
			return "Collect Data";
		}

		public boolean isEndpoint()
		{
			return true;
		}

		public void setup()
		{
			super.setup();
			m_datas = new ArrayList<T>();
		}

		public void clear()
		{
			m_datas.clear();
			m_datas = null;
			super.clear();
		}
	}

	public static class BPDataConsumerTextCollector extends BPDataConsumerBase<String>
	{
		protected volatile StringBuilder m_sb;
		protected volatile String m_text;
		protected volatile String m_sp;

		public String getText()
		{
			return m_text;
		}

		public void setup()
		{
			super.setup();
			StringBuilder sb = new StringBuilder();
			m_sb = sb;
		}

		public void finish()
		{
			m_text = m_sb.toString();
		}

		public void clear()
		{
			m_sb = null;
			m_text = null;
			super.clear();
		}

		public void accept(String t)
		{
			if (m_sp != null)
				if (m_sb.length() > 0)
					m_sb.append(m_sp);
			m_sb.append(t);
		}

		public String getInfo()
		{
			return "Collect Text";
		}

		public boolean isEndpoint()
		{
			return true;
		}
	}

	public static class BPDataConsumerByteArrayCollector extends BPDataConsumerBase<byte[]>
	{
		protected volatile ByteArrayOutputStream m_bos;
		protected volatile byte[] m_bs;

		public byte[] getByteArray()
		{
			return m_bs;
		}

		public void setup()
		{
			super.setup();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			m_bos = bos;
		}

		public void finish()
		{
			m_bs = m_bos.toByteArray();
		}

		public void clear()
		{
			m_bos = null;
			m_bs = null;
			super.clear();
		}

		public void accept(byte[] bs)
		{
			try
			{
				m_bos.write(bs);
			}
			catch (IOException e)
			{
			}
		}

		public String getInfo()
		{
			return "Collect byte[]";
		}
	}
}
