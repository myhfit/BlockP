package bp.data;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.function.BiFunction;

import bp.res.BPResource;
import bp.res.BPResourceFileLocal;
import bp.res.BPResourceHolder;
import bp.util.CachedMap;
import bp.util.IOUtil;
import bp.util.Std;

public class BPDataContainerRandomAccessBase extends BPDataContainerBase implements BPDataContainerRandomAccess
{
	public int read(long pos, byte[] bs, int offset, int len)
	{
		BPResource res = m_res;
		if (res.isIO() && res.isFileSystem() && res.isLocal() && res.isLeaf())
		{
			return ((BPResourceFileLocal) m_res).useRandomAccess((io) ->
			{
				return IOUtil.read(io, pos, bs, offset, len);
			});
		}
		else if (res instanceof BPResourceHolder && ((BPResourceHolder) res).isHold(byte[].class))
		{
			byte[] d = ((BPResourceHolder) res).getData();
			int dlen = d.length;
			int tr = dlen - (int) pos;
			if (tr > len)
				tr = len;
			System.arraycopy(d, (int) pos, bs, offset, tr);
			return tr;
		}
		return -1;
	}

	public void write(long pos, byte[] bs, int offset, int len)
	{
	}

	public long length()
	{
		BPResource res = m_res;
		if (res.isIO() && res.isFileSystem() && res.isLocal() && res.isLeaf())
		{
			return ((BPResourceFileLocal) res).useRandomAccess((io) ->
			{
				try
				{
					return io.getChannel().size();
				}
				catch (IOException e)
				{
					Std.err(e);
				}
				return -1L;
			});
		}
		else if (res instanceof BPResourceHolder && ((BPResourceHolder) res).isHold(byte[].class))
		{
			byte[] bs = ((BPResourceHolder) res).getData();
			return bs.length;
		}
		return -1;
	}

	public static class BPBlockCache
	{
		protected CachedMap<Long, byte[]> m_kv;
		protected int m_blocksize;
		protected long m_len;
		protected WeakReference<BiFunction<Long, Integer, byte[]>> m_reader;

		public BPBlockCache(int blocksize, long len, BiFunction<Long, Integer, byte[]> reader)
		{
			m_blocksize = blocksize;
			m_len = len;
			m_kv = new CachedMap<Long, byte[]>();
			m_kv.setCacheSize(64);
			m_reader = new WeakReference<BiFunction<Long, Integer, byte[]>>(reader);
		}

		public byte[] get(long pos, int size)
		{
			byte[] rc = null;
			int blocksize = m_blocksize;
			long py = (pos % blocksize);
			long end = pos + size;
			if (end > m_len)
				end = m_len;
			long py2 = (end % blocksize);
			if (py <= Integer.MAX_VALUE && py2 <= Integer.MAX_VALUE)
			{
				rc = new byte[size];
				int y = (int) py;
				int y2 = (int) py2;
				long index = pos - y;
				long lastindex = end - y2;
				BiFunction<Long, Integer, byte[]> reader = m_reader.get();
				byte[] bs = new byte[blocksize];
				int x = 0;
				int x0;
				int x1;
				rc = new byte[size];
				for (long i = index; i <= lastindex; i += blocksize)
				{
					if (m_kv.containsKey(i))
						bs = m_kv.get(i);
					else
					{
						bs = reader.apply(i, blocksize);
						m_kv.put(i, bs);
					}
					x0 = i == index ? y : 0;
					x1 = i == lastindex ? y2 : blocksize;
					System.arraycopy(bs, x0, rc, x, x1 - x0);
					x += (x1 - x0);
				}
			}
			return rc;
		}
	}
}
