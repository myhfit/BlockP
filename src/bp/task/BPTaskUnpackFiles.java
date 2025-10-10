package bp.task;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import bp.BPCore;
import bp.context.BPFileContext;
import bp.res.BPResourceDir;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceIO;
import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.FileUtil;
import bp.util.IOUtil;
import bp.util.LogicUtil;
import bp.util.ObjUtil;
import bp.util.Std;

public class BPTaskUnpackFiles extends BPTaskLocal<Boolean>
{
	public BPTaskUnpackFiles()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "Unpack Files";
	}

	protected String[] getPackList(String comment) throws IOException
	{
		if (comment != null && comment.startsWith("srclist:"))
			return comment.substring(8).trim().split(";");
		return null;
	}

	protected OVERWRITE_MODE getOWMode(Object[] ps)
	{
		return ps.length > 5 ? ObjUtil.enumFromOrdinal(OVERWRITE_MODE.class, LogicUtil.NVL((Integer) ps[5], 0)) : OVERWRITE_MODE.REPLACE;
	}

	protected void doStart()
	{
		Object[] ps = (Object[]) m_params;
		String src = (String) ps[0];
		String srcdir = (String) ps[1];
		String[] tars = (String[]) ps[2];
		String tarbase = (String) ps[3];
		String[] srclist = null;
		if (tarbase == null || tarbase.trim().length() == 0)
			tarbase = "./";
		if (srcdir == null || srcdir.trim().length() == 0)
			srcdir = "./";
		if (tars != null && tars.length == 0)
			tars = null;
		boolean ispacklist = ps.length > 4 ? ObjUtil.toBool(ps[4], false) : false;
		OVERWRITE_MODE owmode = getOWMode(ps);
		BPFileContext context = (BPFileContext) getContext();
		if (context == null)
			context = BPCore.getFileContext();
		BPResourceDir srcdirres = (BPResourceDir) context.getRes(srcdir);
		BPResourceFileSystem srcres = srcdirres.getChild(src, false);
		if (!srcres.exists() || !srcres.isFile())
		{
			RuntimeException re = new RuntimeException("Source not exist");
			setFailed(re);
			m_future.completeExceptionally(re);
		}
		else
		{
			setStarted();

			BPResourceDir tarbasedir = (BPResourceDir) context.getRes(tarbase);
			if (tarbasedir == null)
			{
				tarbasedir = context.getDir(tarbase);
				tarbasedir.makeDir();
			}

			Exception innere = null;
			if (ispacklist)
			{
				try (ZipFile zf = new ZipFile(srcres.getFileFullName()))
				{
					srclist = getPackList(zf.getComment());
					if (tars == null)
					{
						innere = new RuntimeException("Pack list not found");
					}
				}
				catch (Exception e)
				{
					innere = e;
				}
			}
			if (innere == null)
			{
				String[] ftars = tars;
				String[] fsrclist = srclist;
				BPResourceDir ftarbasedir = tarbasedir;
				innere = ((BPResourceIO) srcres).useInputStream(in ->
				{
					try (ZipInputStream zis = new ZipInputStream(in))
					{
						unpack(zis, ftars, ftarbasedir, ispacklist, fsrclist, owmode);
					}
					catch (Exception e)
					{
						return e;
					}
					return null;
				});
			}
			if (innere != null)
			{
				setFailed(innere);
				m_future.completeExceptionally(innere);
			}
			else
			{
				setCompleted();
				m_future.complete(true);
			}
		}
	}

	protected BPResourceDir cleanDir(BPResourceDir d)
	{
		BPResourceFileSystem[] ress = d.list();
		if (ress != null)
		{
			for (BPResourceFileSystem res : ress)
			{
				Std.debug("clean:" + res.getFileFullName());
				res.delete(true);
			}
		}
		return d;
	}

	protected BPResourceDir getTargetDir(BPResourceDir base, String tar, String subpath)
	{
		String t = tar;
		if (".".equals(t) || "./".equals(t))
			t = "";
		if (t.length() > 0 && (!(t.endsWith("/") || t.endsWith(File.separator))))
			t += "/";
		t += subpath;
		if (t.length() > 0)
			return base.getDir(t);
		else
			return base;
	}

	protected void unpack(ZipInputStream zis, String[] tars, BPResourceDir ftarbasedir, boolean ispacklist, String[] srclist, OVERWRITE_MODE owmode) throws Exception
	{
		for (String tar : tars)
		{
			Map<String, File> pathmap = new HashMap<String, File>();
			BPResourceDir ftardir=getTargetDir(ftarbasedir, tar, "");
			if (ispacklist)
			{
				for (String src : srclist)
				{
					String rsrc = src.replaceAll("\\\\", "/");
					if (!rsrc.endsWith("/"))
						rsrc += "/";
					if (owmode == OVERWRITE_MODE.CLEAN)
						pathmap.put(rsrc, new File(cleanDir(getTargetDir(ftarbasedir, tar, src)).getFileFullName()));
					else
						pathmap.put(rsrc, new File(getTargetDir(ftarbasedir, tar, src).getFileFullName()));
				}
			}
			else
			{
				if (owmode == OVERWRITE_MODE.CLEAN)
					cleanDir(ftardir);
			}

			ZipEntry e = zis.getNextEntry();

			if (!ispacklist)
			{
				File fbase = new File(ftardir.getFileFullName());
				while (e != null)
				{
					writeEntry(e, zis, fbase, e.getName());
					e = zis.getNextEntry();
				}
			}
			else
			{
				while (e != null)
				{
					String key = e.getName();
					key = key.replaceAll("\\\\", "/");
					File tardir = null;
					String subpath = null;
					for (String sk : pathmap.keySet())
					{
						if (key.startsWith(sk) && key.length() > sk.length())
						{
							tardir = pathmap.get(sk);
							subpath = key.substring(sk.length());
							break;
						}
					}
					if (tardir != null)
					{
						writeEntry(e, zis, tardir, subpath);
					}
					e = zis.getNextEntry();
				}
			}
		}
	}

	protected void writeEntry(ZipEntry e, ZipInputStream zis, File basedir, String name)
	{
		File nf = new File(basedir, name);
		if (e.isDirectory())
		{
			Std.debug("[D]" + nf.getAbsolutePath());
			if (!nf.exists())
				nf.mkdirs();
		}
		else
		{
			byte[] bs = IOUtil.read(zis);
			if (bs == null)
				throw new RuntimeException("Can't read bytes from " + name);
			Std.debug("[F]" + nf.getAbsolutePath());
			FileUtil.writeFile(nf.getAbsolutePath(), bs);
		}
	}

	public boolean needConfirm()
	{
		OVERWRITE_MODE owmode = getOWMode((Object[]) m_params);
		return owmode == OVERWRITE_MODE.CLEAN || owmode == OVERWRITE_MODE.REPLACE;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		Object[] ps = (Object[]) m_params;
		if (ps != null && ps.length > 1)
		{
			String src = (String) ps[0];
			String srcbase = (String) ps[1];
			String[] tars = (String[]) ps[2];
			String tardir = (String) ps[3];
			rc.put("source", src);
			rc.put("sourcedir", srcbase);
			rc.put("target", String.join(";", tars));
			rc.put("targetbase", tardir);
			if (ps.length > 4)
				rc.put("readpacklist", ObjUtil.toBool(ps[4], false));
			if (ps.length > 5)
				rc.put("owmode", LogicUtil.CHAIN_NN(ps[5], v1 -> ObjUtil.enumFromOrdinal(OVERWRITE_MODE.class, (Integer) v1), e -> ((OVERWRITE_MODE) e).name()));
		}
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		String srcstr = (String) data.get("source");
		String srcbase = (String) data.get("sourcedir");
		String tarstr = (String) data.get("target");
		String tardir = (String) data.get("targetbase");
		String owmode = (String) data.get("owmode");
		boolean packlist = ObjUtil.toBool(data.get("readpacklist"), false);

		String[] tars = tarstr == null ? null : tarstr.trim().split(";");
		m_params = new Object[] { srcstr, srcbase, tars, tardir != null ? tardir.trim() : null, packlist, LogicUtil.IFVR(ObjUtil.enumValueOf(OVERWRITE_MODE.class, owmode), e -> e == null ? 0 : e.ordinal()) };
	}

	public static class BPTaskFactoryUnpackFiles extends BPTaskFactoryBase<BPTaskUnpackFiles>
	{
		public String getName()
		{
			return "Unpack Files";
		}

		protected BPTaskUnpackFiles createTask()
		{
			return new BPTaskUnpackFiles();
		}

		public Class<? extends BPTask<?>> getTaskClass()
		{
			return BPTaskUnpackFiles.class;
		}
	}

	public static enum OVERWRITE_MODE
	{
		REPLACE, NOTREPLACE, CLEAN, CONFIRM
	}
}