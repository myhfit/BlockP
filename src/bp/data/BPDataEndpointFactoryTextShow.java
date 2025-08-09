package bp.data;

import java.util.List;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.data.BPDataConsumer.BPDataConsumerByteArrayCollector;
import bp.data.BPDataConsumer.BPDataConsumerTextCollector;
import bp.format.BPFormatText;
import bp.format.BPFormatUnknown;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPDataEndpointFactoryTextShow implements BPDataEndpointFactory
{
	public String getName()
	{
		return "Show Text";
	}

	@SuppressWarnings("unchecked")
	public <D> BPDataConsumer<D> create(String formatname)
	{
		if (BPFormatText.FORMAT_TEXT.equals(formatname))
			return (BPDataConsumer<D>) new BPDataConsumerTextShow();
		else if (BPFormatUnknown.FORMAT_NA.equals(formatname))
			return (BPDataConsumer<D>) new BPDataConsumerBS2TextShow();
		return null;
	}

	public List<String> getSupportedFormats()
	{
		return ObjUtil.makeList(BPFormatText.FORMAT_TEXT, BPFormatUnknown.FORMAT_NA);
	}

	public static class BPDataConsumerTextShow extends BPDataConsumerTextCollector
	{
		public void finish()
		{
			super.finish();
			Std.info_user(m_text);
		}

		public String getInfo()
		{
			return "Show Text";
		}

		public boolean isEndpoint()
		{
			return true;
		}
	}

	public static class BPDataConsumerBS2TextShow extends BPDataConsumerByteArrayCollector
	{
		protected volatile String m_encoding = null;

		public void finish()
		{
			super.finish();
			Std.info_user(TextUtil.toString(m_bs, m_encoding == null ? "utf-8" : m_encoding));
		}

		public String getInfo()
		{
			return "Show byte[](Text)";
		}

		public BPSetting getSetting()
		{
			BPSettingBase rc = new BPSettingBase().addItem(BPSettingItem.create("encoding", "Encoding", BPSettingItem.ITEM_TYPE_TEXT, null));
			rc.set("encoding", m_encoding);
			return rc;
		}

		public void setSetting(BPConfig cfg)
		{
			String encoding = (String) cfg.get("encoding");
			if (encoding != null && encoding.length() == 0)
				encoding = null;
			m_encoding = encoding;
		}

		public boolean isEndpoint()
		{
			return true;
		}
	}
}