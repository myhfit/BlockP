package bp.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import bp.BPCore;
import bp.format.BPFormatDir;
import bp.res.BPResource;
import bp.res.BPResourceByteArray;
import bp.res.BPResourceIO;
import bp.util.FileUtil;
import bp.util.IOUtil;
import bp.util.Std;

public class BPDataContainerArchive extends BPDataContainerBase implements BPDataContainerFileSystem
{
	protected ConcurrentHashMap<String, ArchiveEntry> m_datamap = new ConcurrentHashMap<String, ArchiveEntry>();

	public void readFull(BiPredicate<String, Boolean> filter)
	{
		BPResourceIO resio = (BPResourceIO) m_res;

		resio.useInputStream(in ->
		{
			try (ZipInputStream zis = new ZipInputStream(in))
			{
				ZipEntry entry = zis.getNextEntry();
				while (entry != null)
				{
					String ename = entry.getName();
					boolean isdir = entry.isDirectory();
					if (filter.test(ename, isdir))
					{
						if (!isdir)
						{
							m_datamap.put(ename, new ArchiveEntry(false, IOUtil.read(zis)));
						}
						else
						{
							m_datamap.put(ename, new ArchiveEntry(true, null));
						}
					}
					entry = zis.getNextEntry();
				}
			}
			catch (IOException e)
			{
				Std.err(e);
			}
			return null;
		});
	}

	public BPResource[] listResources()
	{
		List<String> names = Collections.list(m_datamap.keys());
		names.sort(String.CASE_INSENSITIVE_ORDER);
		List<BPResource> rc = new ArrayList<BPResource>();
		for (String name : names)
		{
			ArchiveEntry entry = m_datamap.get(name);
			byte[] bs = entry.bs;
			String ext = entry.isdir ? BPFormatDir.FORMAT_DIR : FileUtil.getExt(name);
			BPResourceByteArray res = new BPResourceByteArray(bs, null, ext, BPCore.genID(BPCore.getFileContext()), name, bs != null);
			rc.add(res);
		}
		return rc.toArray(new BPResource[rc.size()]);
	}

	public void close()
	{
		m_datamap.clear();
		m_datamap = null;
	}

	public void open()
	{
	}

	protected static class ArchiveEntry
	{
		public boolean isdir;
		public byte[] bs;

		public ArchiveEntry()
		{
		}

		public ArchiveEntry(boolean isdir, byte[] bs)
		{
			this.isdir = isdir;
			this.bs = bs;
		}
	}
}
