package bp.format;

import static bp.util.LockUtil.rwLock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import bp.data.BPMData;
import bp.util.ClassUtil;
import bp.util.FileUtil;

public class BPFormatManager implements BPMData
{
	protected final static Map<String, BPFormat> S_FORMATEXTMAP = new ConcurrentHashMap<String, BPFormat>();
	protected final static List<BPFormat> S_FORMATS = new CopyOnWriteArrayList<BPFormat>();

	protected final static ReadWriteLock S_LOCK = new ReentrantReadWriteLock();

	public final static void init()
	{
		ServiceLoader<BPFormatFactory> facs = ClassUtil.getExtensionServices(BPFormatFactory.class);
		if (facs != null)
		{
			rwLock(S_LOCK, true, () ->
			{
				S_FORMATEXTMAP.clear();
				S_FORMATS.clear();
				List<BPFormat> fs = new ArrayList<BPFormat>();
				for (BPFormatFactory fac : facs)
				{
					fac.register((format) ->
					{
						registerFormat(format);
						fs.add(format);
					});
				}
				S_FORMATS.addAll(fs);
			});
		}
	}

	private final static void registerFormat(BPFormat format)
	{
		String[] exts = format.getExts();
		rwLock(S_LOCK, true, () ->
		{
			if (exts != null)
			{
				for (String ext : exts)
				{
					S_FORMATEXTMAP.put(ext, format);
				}
			}
		});
	}

	public final static List<BPFormat> getFormats()
	{
		return rwLock(S_LOCK, false, () ->
		{
			return new ArrayList<BPFormat>(S_FORMATS);
		});
	}

	public final static BPFormat getFormatByExt(String ext)
	{
		BPFormat format = null;
		if (ext != null)
		{
			String fext = (ext.startsWith(".") && FileUtil.isIgnoreSensitive()) ? ext.toLowerCase() : ext;
			format = rwLock(S_LOCK, false, () -> S_FORMATEXTMAP.get(fext));
			if (format == null)
			{
				List<BPFormat> formats = getFormats();
				for (BPFormat f : formats)
				{
					if (f.canCover(fext))
					{
						format = f;
						break;
					}
				}
			}
		}
		return format != null ? format : new BPFormatUnknown();
	}

	public final static List<BPFormat> getFormatsByFeature(BPFormatFeature feature)
	{
		List<BPFormat> formats = getFormats();
		List<BPFormat> rc = new ArrayList<BPFormat>();
		for (BPFormat f : formats)
		{
			if (f.checkFeature(feature))
			{
				rc.add(f);
			}
		}
		return rc;
	}

	public final static BPFormat getFormatByName(String name)
	{
		BPFormat format = null;
		List<BPFormat> formats = getFormats();
		for (BPFormat f : formats)
		{
			if (name.equals(f.getName()))
			{
				format = f;
				break;
			}
		}
		return format != null ? format : new BPFormatUnknown();
	}

	public final static class BPFormatFactoryCore implements BPFormatFactory
	{
		public void register(Consumer<BPFormat> regfunc)
		{
			regfunc.accept(new BPFormatText());
			regfunc.accept(new BPFormatCSV());
			regfunc.accept(new BPFormatTSV());
			regfunc.accept(new BPFormatJSON());
			regfunc.accept(new BPFormatZip());
			regfunc.accept(new BPFormatGZip());
			regfunc.accept(new BPFormatXYData());
			regfunc.accept(new BPFormatTreeData());
			regfunc.accept(new BPFormatDir());
			regfunc.accept(new BPFormatProject());
			regfunc.accept(new BPFormatBPPD());
		}
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rwLock(S_LOCK, false, () -> rc.putAll(S_FORMATEXTMAP));
		return rc;
	}
}
