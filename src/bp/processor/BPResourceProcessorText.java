package bp.processor;

import java.util.ArrayList;
import java.util.List;

import bp.config.BPConfig;
import bp.config.BPConfigSimple;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.data.BPDataContainerFactory;
import bp.data.BPXData;
import bp.data.BPXYContainer;
import bp.data.BPXYData.BPXYDataList;
import bp.format.BPFormatText;
import bp.format.BPFormatXYData;
import bp.res.BPResource;
import bp.res.BPResourceFactory;
import bp.res.BPResourceHolder;
import bp.res.BPResourceHolder.BPResourceHolderW;
import bp.res.BPResourceIO;
import bp.util.ClassUtil;
import bp.util.IOUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

abstract class BPResourceProcessorTextBase implements BPResourceProcessor<BPResource, BPResource>
{
	public String getCategory()
	{
		return BPFormatText.FORMAT_TEXT;
	}

	public boolean canInput(String format)
	{
		return format.equals(BPFormatText.FORMAT_TEXT);
	}

	protected String readText(BPResource data, BPConfig config)
	{
		String txt = null;
		boolean readed = false;
		if (data instanceof BPResourceHolder)
		{
			Object obj = ((BPResourceHolder) data).getData();
			if (obj != null)
			{
				if (obj instanceof String)
				{
					txt = (String) obj;
					readed = true;
				}
			}
		}
		if (!readed && data.isIO())
		{
			BPResourceIO source = (BPResourceIO) data;
			txt = source.useInputStream(in -> TextUtil.toString(IOUtil.read(in), "utf-8"));
		}
		return txt;
	}
}

public abstract class BPResourceProcessorText extends BPResourceProcessorTextBase
{
	public boolean canOutput(String format)
	{
		return format.equals(BPFormatText.FORMAT_TEXT);
	}

	public BPResource process(BPResource data, BPConfig config)
	{
		String txt = readText(data, config);
		if (txt != null)
		{
			txt = dealText(txt);
		}
		BPResourceHolder.BPResourceHolderW rc = null;
		Object out = config.get("OUTPUT");
		if (out != null)
			rc = (BPResourceHolderW) out;
		else
			rc = new BPResourceHolder.BPResourceHolderW(null, null, BPFormatText.MIME_TEXT, null, null, true);
		rc.setData(txt);
		return rc;
	}

	public BPSetting getSetting(BPConfig preset)
	{
		BPSettingBase rc = new BPSettingBase(preset);
		rc.addItem(BPSettingItem.create("OUTPUT", "Output Resource", BPSettingItem.ITEM_TYPE_TEXT, null));
		return rc;
	}

	protected abstract String dealText(String txt);

	public final static class BPResourceProcessorTextUpperCase extends BPResourceProcessorText
	{
		public String getName()
		{
			return "Text Processor - To Upper Case";
		}

		protected String dealText(String txt)
		{
			return txt.toUpperCase();
		}

		public String getUILabel()
		{
			return "To Upper Case";
		}
	}

	public final static class BPResourceProcessorTextLowerCase extends BPResourceProcessorText
	{
		public String getName()
		{
			return "Text Processor - To Lower Case";
		}

		protected String dealText(String txt)
		{
			return txt.toLowerCase();
		}

		public String getUILabel()
		{
			return "To Lower Case";
		}
	}

	public final static class BPResourceProcessorText2XY extends BPResourceProcessorTextBase
	{
		public String getName()
		{
			return "Text Processor - Export XY";
		}

		public String getUILabel()
		{
			return "Export XY";
		}

		public boolean needSettingUI()
		{
			return true;
		}

		public BPResource process(BPResource data, BPConfig config)
		{
			String txt = readText(data, config);
			BPResource outres = config.get("OUTPUT");
			return writeXYData(outres, txt, config);
		}

		protected BPResource writeXYData(BPResource out, String txt, BPConfig config)
		{
			String facname = config.get("OUTPUT_TYPE");
			BPDataContainerFactory fac = ClassUtil.findService(BPDataContainerFactory.class, c -> c.getName().equals(facname));
			if (fac != null)
			{
				BPSetting setting = fac.getSetting();
				if (setting != null)
				{
				}
				BPXYContainer con = fac.createContainer(setting);
				con.bind(out.isFactory() ? ((BPResourceFactory) out).makeResource(BPConfigSimple.fromData(ObjUtil.makeMap("format", fac.getFormat()))) : out);
				con.open();
				List<BPXData> datas = new ArrayList<BPXData>();
				String[] lines = txt.split("\n");
				for (int i = 0; i < lines.length; i++)
				{
					String line = lines[i];
					if (line.endsWith("\r"))
					{
						line = line.substring(0, line.length() - 1);
					}
					BPXData linedata = new BPXData.BPXDataArray(new Object[] { line });
					datas.add(linedata);
				}
				BPXYDataList list = new BPXYDataList(new Class<?>[] { String.class }, new String[] { "TEXT" }, null, datas, true);
				con.writeXYData(list);
				con.close();
				return out;
			}
			return null;
		}

		public BPSetting getSetting(BPConfig preset)
		{
			BPSettingBase rc = new BPSettingBase(preset);
			List<BPDataContainerFactory> facs = ClassUtil.filterServices(BPDataContainerFactory.class, (c) ->
			{
				return c.canHandle(BPFormatXYData.FORMAT_XYDATA);
			});
			String[] sels = new String[facs.size()];
			for (int i = 0; i < facs.size(); i++)
			{
				BPDataContainerFactory fac = facs.get(i);
				sels[i] = fac.getName();
			}
			rc.addItem(BPSettingItem.create("OUTPUT_TYPE", "Output Resource Type", BPSettingItem.ITEM_TYPE_SELECT, sels));
			rc.addItem(BPSettingItem.create("OUTPUT", "Output Resource", BPSettingItem.ITEM_TYPE_RESOURCE_SAVE, null));
			return rc;
		}
	}
}
