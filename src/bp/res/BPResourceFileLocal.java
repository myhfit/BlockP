package bp.res;

import static bp.util.Std.err;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Function;

import bp.util.Std;

public class BPResourceFileLocal extends BPResourceFileSystemLocal implements BPResourceFile
{
	public BPResourceFileLocal(String filename)
	{
		m_file = new File(filename);
	}

	public BPResourceFileLocal(String path, String filename)
	{
		m_file = new File(path, filename);
	}

	public BPResourceFileLocal(File f)
	{
		m_file = f;
	}

	public <T> T useRandomAccess(Function<RandomAccessFile, T> io)
	{
		try (RandomAccessFile raf = new RandomAccessFile(m_file, "rw"))
		{
			return io.apply(raf);
		}
		catch (FileNotFoundException e)
		{
			err(e);
		}
		catch (IOException e)
		{
			err(e);
		}
		return null;
	}

	public <T> T useInputStream(Function<InputStream, T> in)
	{
		try (FileInputStream fis = new FileInputStream(m_file))
		{
			return in.apply(fis);
		}
		catch (FileNotFoundException e)
		{
			err(e);
		}
		catch (IOException e)
		{
			err(e);
		}
		return null;
	}

	public <T> T useOutputStream(Function<OutputStream, T> out)
	{
		try (FileOutputStream fos = new FileOutputStream(m_file))
		{
			return out.apply(fos);
		}
		catch (FileNotFoundException e)
		{
			err(e);
		}
		catch (IOException e)
		{
			err(e);
		}
		return null;
	}

	public String getExt()
	{
		String filename = m_file.getName();
		String ext = "";
		int vi = filename.lastIndexOf(".");
		if (vi > -1)
			ext = filename.substring(vi);
		return ext;
	}

	public long getSize()
	{
		return m_file.length();
	}

	public String toString()
	{
		return m_file.getName();
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> map = super.getMappedData();
		map.put("len", getSize());
		map.put("lastmodified", getLastModified());
		return map;
	}

	public boolean copy(BPResource target)
	{
		boolean success = true;
		if (target.isFileSystem())
		{
			BPResourceFileSystem fs = (BPResourceFileSystem) target;
			if (fs.isDirectory())
			{
				BPResourceDir tdir = (BPResourceDir) target;
				BPResourceFileSystem nfs = tdir.getChild(m_file.getName(), false);
				try
				{
					Files.copy(m_file.toPath(), new File(nfs.getFileFullName()).toPath());
				}
				catch (IOException e)
				{
					success = false;
					Std.err(e);
				}
			}
			else
			{
				try
				{
					Files.copy(m_file.toPath(), new File(fs.getFileFullName()).toPath());
				}
				catch (IOException e)
				{
					success = false;
					Std.err(e);
				}
			}
		}
		return success;
	}
}
