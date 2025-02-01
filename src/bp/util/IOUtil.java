package bp.util;

import static bp.util.Std.err;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.function.Function;

import bp.res.BPResourceIO;

public class IOUtil
{
	public final static byte[] read(BPResourceIO io)
	{
		byte[] rc = null;
		if (io != null)
		{
			if (io.canOpen())
				rc = io.useInputStream(in -> read(in));
		}
		return rc;
	}

	public final static byte[] read(InputStream in)
	{
		byte[] bs = null;
		try
		{
			BufferedInputStream bis = new BufferedInputStream(in);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[65536];
			int len = bis.read(b);
			while (len >= 0)
			{
				bos.write(b, 0, len);
				len = bis.read(b);
			}
			bs = bos.toByteArray();
		}
		catch (IOException e)
		{
			err(e);
		}
		return bs;
	}

	public final static boolean write(BPResourceIO io, byte[] bs)
	{
		if (io == null)
			return false;
		return io.useOutputStream(out -> write(out, bs));
	}

	public final static boolean write(OutputStream out, byte[] bs)
	{
		try
		{
			BufferedOutputStream bos = new BufferedOutputStream(out);
			bos.write(bs);
			bos.flush();
			return true;
		}
		catch (IOException e)
		{
			err(e);
		}
		return false;
	}

	public final static int read(RandomAccessFile raf, long pos, byte[] bs, int offset, int len)
	{
		try
		{
			raf.seek(pos);
			return raf.read(bs, offset, len);
		}
		catch (IOException e)
		{
			err(e);
		}
		return -1;
	}

	public final static void interReadLines(InputStream in, String encoding, Function<String, Boolean> callback)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
			String line = reader.readLine();
			while (callback.apply(line))
			{
				line = reader.readLine();
			}
		}
		catch (IOException e)
		{
			err(e);
		}
	}

	public final static void close(Closeable io)
	{
		if (io == null)
			return;
		try
		{
			io.close();
		}
		catch (Error | Exception e)
		{
		}
	}

	public static abstract class ReadResourceResult
	{
		public abstract int read(byte[] buffer);

		public boolean isdir;
		public String res;
	}

	public static class ReadResourceResultHolder extends ReadResourceResult
	{
		public byte[] datas;
		protected int pos;

		public int read(byte[] buffer)
		{
			int len = buffer.length;
			int rl = datas.length;
			if (pos >= rl)
			{
				return -1;
			}
			else
			{
				int copylen = Math.min(len, rl - pos);
				System.arraycopy(datas, pos, buffer, 0, copylen);
				pos += copylen;
				return copylen;
			}
		}

	}

	public final static String PATH_TYPE_LOCALFS = "LOCALFS";
	public final static String PATH_TYPE_ZIP = "ZIP";
}
