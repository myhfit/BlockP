package bp.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import bp.BPCore;
import bp.context.BPFileContext;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceIO;
import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.IOUtil;
import bp.util.ObjUtil;
import bp.util.Std;

public class BPTaskPackFiles extends BPTaskLocal<Boolean>
{
	public BPTaskPackFiles()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "Pack Files";
	}

	protected final static String comparePath(String base, String filename)
	{
		int l = base.length();
		if (filename.startsWith(base))
		{
			if (base.endsWith("/") || base.endsWith(File.separator))
				return filename.substring(l);
			else
			{
				if (filename.length() == l)
				{
					return null;
				}
				char c = filename.charAt(l);
				if (c == '/' || c == File.separatorChar)
					return filename.substring(base.length() + 1);
				else
					throw new RuntimeException("Source Base Error");
			}
		}
		else
			throw new RuntimeException("Source Base Error");
	}

	protected void doStart()
	{
		Object[] ps = (Object[]) m_params;
		String[] srcs = (String[]) ps[0];
		String srcbase = (String) ps[1];
		String tar = (String) ps[2];
		String tardir = (String) ps[3];
		if (srcbase != null && srcbase.trim().length() == 0)
			srcbase = null;
		if (tardir == null || tardir.trim().length() == 0)
			tardir = "./";
		BPFileContext context = (BPFileContext) getContext();
		if (context == null)
			context = BPCore.getFileContext();
		BPResourceDir dir = (BPResourceDir) context.getRes(tardir);
		if (dir == null)
		{
			dir = context.getDir(tardir);
			dir.makeDir();
		}
		BPResourceFileSystem tres = dir.getChild(tar, false);
		if (tres.exists() && !tres.isFile())
		{
			RuntimeException re = new RuntimeException("target not exist");
			setFailed(re);
			m_future.completeExceptionally(re);
		}
		else if (srcs.length > 0)
		{
			setStarted();
			BPFileContext fcontext = context;
			BPResourceFileSystem srcbaseres = null;
			if (srcbase != null && srcbase.trim().length() > 0)
				srcbaseres = (BPResourceFileSystem) fcontext.getRes(srcbase);
			String srcbasepath = (srcbaseres != null && srcbaseres.exists() && srcbaseres.isDirectory()) ? srcbaseres.getFileFullName() : null;
			Exception innere = ((BPResourceIO) tres).useOutputStream((out) ->
			{
				ZipOutputStream zos = null;
				int c = 0;
				try
				{
					zos = new ZipOutputStream(out);
					List<BPResourceFileSystem> fs = new ArrayList<BPResourceFileSystem>();
					List<String> pars = new ArrayList<String>();
					for (String src : srcs)
					{
						BPResourceFileSystem root = (BPResourceFileSystem) fcontext.getRes(src);
						collectParent(root, fcontext, fs, pars, srcbasepath);
						fs.add(root);
						collectAll(root, fs);
					}
					int count = fs.size();
					for (BPResourceFileSystem f : fs)
					{
						String filename = f.getFileFullName();
						String relpath = srcbasepath != null ? comparePath(srcbasepath, filename) : fcontext.comparePath(filename);
						if (relpath != null)
						{
							if (File.separator.equals("\\"))
							{
								relpath = relpath.replaceAll("\\\\", "/");
							}
							String entryname = relpath;
							if (f.isDirectory() && !entryname.endsWith("/"))
								entryname += "/";
							ZipEntry entry = new ZipEntry(entryname);
							Class<BasicFileAttributes> bfacls = BasicFileAttributes.class;
							BasicFileAttributes attrs = Files.readAttributes(Paths.get(filename), bfacls);
							entry.setCreationTime(attrs.creationTime());
							entry.setLastModifiedTime(attrs.lastModifiedTime());
							zos.putNextEntry(entry);
							if (f.isFile())
							{
								zos.write(((BPResourceIO) f).useInputStream((in) -> IOUtil.read(in)));
							}
							zos.closeEntry();
						}
					}
					setProgress((float) ++c / (float) count);
					setProgressText(c + "/" + count);
					out.flush();
					zos.close();
				}
				catch (Exception e)
				{
					if (zos != null)
					{
						try
						{
							zos.close();
						}
						catch (IOException e1)
						{
							Std.err(e1);
						}
					}
					return e;
				}
				return null;
			});
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

	protected final static void collectParent(BPResourceFileSystem node, BPFileContext fcontext, List<BPResourceFileSystem> fs, List<String> pars, String srcbasepath)
	{
		String filename = node.getFileFullName();
		String relpath = srcbasepath != null ? comparePath(srcbasepath, filename) : fcontext.comparePath(filename);
		List<String> pps = getParentPath(relpath, filename);
		for (int i = pps.size() - 1; i > -1; i--)
		{
			String p = pps.get(i);
			if (!pars.contains(p))
			{
				pars.add(p);
				fs.add((BPResourceFileSystem) fcontext.getRes("@" + p));
			}
		}
	}

	protected final static List<String> getParentPath(String relpath, String filename)
	{
		List<String> rc = new ArrayList<String>();
		String str = relpath;
		String fname = filename;
		char ch = File.separatorChar;
		if (ch != '/')
		{
			str = str.replace(ch, '/');
			fname = fname.replace(ch, '/');
		}
		if (str.endsWith("/"))
			str = str.substring(str.length() - 1);
		if (fname.endsWith("/"))
			fname = fname.substring(fname.length() - 1);
		int vi = str.lastIndexOf("/");
		int vi2;
		String nstr, nfname;
		while (vi > 0)
		{
			nstr = str.substring(0, vi);
			vi2 = fname.lastIndexOf("/");
			nfname = fname.substring(0, vi2);
			rc.add(nfname);
			fname = nfname;
			str = nstr;
			vi = str.lastIndexOf("/");
		}
		return rc;
	}

	protected final static void collectAll(BPResourceFileSystem res, List<BPResourceFileSystem> result)
	{
		BPResource[] chds = res.listResources();
		for (BPResource chd : chds)
		{
			if (chd.isFileSystem())
			{
				BPResourceFileSystem fs = (BPResourceFileSystem) chd;
				boolean isdir = fs.isDirectory();
				if (fs.isFile() || isdir)
				{
					result.add(fs);
					if (isdir)
					{
						collectAll(fs, result);
					}
				}
			}
		}
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		Object[] ps = (Object[]) m_params;
		if (ps != null && ps.length > 1)
		{
			StringBuilder sb = new StringBuilder();
			String[] srcs = (String[]) ps[0];
			String srcbase = (String) ps[1];
			String tar = (String) ps[2];
			String tardir = (String) ps[3];
			for (String src : srcs)
			{
				if (sb.length() > 0)
					sb.append(";");
				sb.append(src);
			}
			rc.put("source", sb.toString());
			rc.put("sourcebase", srcbase);
			rc.put("target", tar);
			rc.put("targetdir", tardir);
			if (ps.length > 4)
				rc.put("packlist", ObjUtil.toBool(ps[4], false));
		}
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		String srcstr = (String) data.get("source");
		String srcbase = (String) data.get("sourcebase");
		String tarstr = (String) data.get("target");
		String tardir = (String) data.get("targetdir");
		boolean packlist = ObjUtil.toBool(data.get("packlist"), false);

		String[] srcs = srcstr == null ? null : srcstr.trim().split(";");
		m_params = new Object[] { srcs, srcbase, tarstr.trim(), tardir != null ? tardir.trim() : null, packlist };
	}

	public static class BPTaskFactoryPackFiles extends BPTaskFactoryBase<BPTaskPackFiles>
	{
		public String getName()
		{
			return "Pack Files";
		}

		protected BPTaskPackFiles createTask()
		{
			return new BPTaskPackFiles();
		}

		public Class<? extends BPTask<?>> getTaskClass()
		{
			return BPTaskPackFiles.class;
		}
	}
}
