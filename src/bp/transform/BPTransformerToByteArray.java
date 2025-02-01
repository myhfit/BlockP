package bp.transform;

import java.nio.ByteBuffer;
import java.util.Date;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.util.DateUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPTransformerToByteArray extends BPTransformerBase<Object>
{
	protected String m_en;

	protected Object transform(Object t)
	{
		if (t == null)
			return null;
		if (t instanceof byte[])
		{
			return t;
		}
		else if (t instanceof Number)
		{
			if (t instanceof Integer)
				return toBS((Integer) t);
			else if (t instanceof Short)
				return toBS((Short) t);
			else if (t instanceof Long)
				return toBS((Long) t);
			else if (t instanceof Float)
				return toBS((Float) t);
			else if (t instanceof Double)
				return toBS((Double) t);
			else
				return toBS(((Number) t).doubleValue());
		}
		else if (t instanceof String)
		{
			return TextUtil.fromString((String) t, getEncoding());
		}
		else if (t instanceof Date)
		{
			return TextUtil.fromString(DateUtil.formatTime(((Date) t).getTime()), getEncoding());
		}
		else if (t.getClass().isPrimitive())
		{
			return ObjUtil.toString(t);
		}
		return null;
	}

	protected String getEncoding()
	{
		return m_en == null ? "utf-8" : m_en;
	}

	protected byte[] toBS(long v)
	{
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(v);
		return bb.array();
	}

	protected byte[] toBS(int v)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(v);
		return bb.array();
	}

	protected byte[] toBS(float v)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putFloat(v);
		return bb.array();
	}

	protected byte[] toBS(double v)
	{
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putDouble(v);
		return bb.array();
	}

	protected byte[] toBS(byte v)
	{
		return new byte[] { v };
	}

	public String getInfo()
	{
		return "Common to byte[]";
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = new BPSettingBase().addItem(BPSettingItem.create("encoding", "Encoding", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.set("encoding", m_en);
		return rc;
	}

	public void setSetting(BPConfig cfg)
	{
		if (cfg != null)
		{
			m_en = cfg.get("encoding");
		}
	}
}
