package bp.cache;

import java.io.File;

public class BPCacheDataFileSystem implements BPCacheDataResource
{
	public String name;
	public int flags;
	public long lastmodified;
	public BPCacheDataFileSystem parent;

	public BPCacheDataFileSystem(BPCacheDataFileSystem par, File f)
	{
		this.parent = par;
		name = par != null ? f.getName() : f.getAbsolutePath();
		boolean isf = f.isFile();
		boolean isd = f.isDirectory();
		boolean ish = f.isHidden();
		boolean isa = f.isAbsolute();
		lastmodified = f.lastModified();
		flags = (isf ? 1 : 0) + (isd ? 2 : 0) + (ish ? 8 : 0) + (isa ? 16 : 0);
	}

	public boolean isFile()
	{
		return (flags & 0x1) == 0x1;
	}

	public boolean isDirectory()
	{
		return (flags & 0x2) == 0x2;
	}

	public boolean isAbsolute()
	{
		return (flags & 0x8) == 0x8;
	}

	public boolean isHidden()
	{
		return (flags & 0x10) == 0x10;
	}

	public String getName()
	{
		return name;
	}

	public String getFullName()
	{
		String parname = (parent != null ? parent.getFullName() : null);
		return (parname == null ? name : (parname + File.separator + name));
	}

	public String getFullPath()
	{
		return (parent != null ? parent.getFullName() : "");
	}

	public boolean isFileSystem()
	{
		return true;
	}

	public boolean isLocal()
	{
		return true;
	}
}
