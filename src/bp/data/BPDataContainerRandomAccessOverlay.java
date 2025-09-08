package bp.data;

import java.io.IOException;
import java.io.OutputStream;

import bp.env.BPEnvCommon;
import bp.env.BPEnvManager;
import bp.res.BPResourceIO;
import bp.util.IOUtil;
import bp.util.ObjUtil;
import bp.util.ResourceUtil;

public class BPDataContainerRandomAccessOverlay extends BPDataContainerOverlay<BPDataContainerRandomAccess> implements BPDataContainerRandomAccess
{
	protected int m_initblocksize;
	protected BPOverlayRABlocks m_blocks;
	protected BPDataContainerRandomAccess m_readsrc;

	public BPDataContainerRandomAccessOverlay()
	{
		m_initblocksize = ObjUtil.toInt(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_RAWIO_BLOCKSIZE), RAWIO_BLOCKSIZE_DEFAULT);
	}

	public BPOverlayRABlocks getBlocks()
	{
		return m_blocks;
	}

	public int read(long pos, byte[] bs, int offset, int len)
	{
		int r = 0;
		long bpos = 0;
		long rpos = pos;
		int rlen = len;
		int dpos = offset;

		BPOverlayRABlocks blocks = m_blocks;
		{
			int blen;
			long bend;
			int bi;
			int bilen;
			int count = blocks.count;
			for (int i = 0; i < count; i++)
			{
				blen = blocks.lengths[i];
				bend = bpos + blen;
				if (rpos >= bpos && rpos < bend)
				{
					byte[] blockbs = blocks.get(i, m_src);
					bi = (int) (rpos - bpos);
					bilen = Math.min(rlen, blen - bi);
					System.arraycopy(blockbs, bi, bs, dpos, bilen);
					r += bilen;
					rpos += bilen;
					rlen -= bilen;
					if (rlen <= 0)
						break;
					dpos += bilen;
				}
				bpos += blen;
			}
		}
		return r;
	}

	public void overwrite(long pos, byte[] bs, int offset, int len)
	{
		m_blocks.overwrite(pos, bs, offset, len, m_src);
	}

	public void replace(long pos, byte[] bs, int offset, int orilen, int len)
	{
		BPDataContainerRandomAccess src = m_src;
		BPOverlayRABlocks blocks = m_blocks;

		int d = len - orilen;
		if (d == 0)
		{
			blocks.overwrite(pos, bs, offset, len, src);
		}
		else if (d < 0)
		{
			long delpos = pos + len;
			blocks.overwrite(pos, bs, offset, len, src);
			blocks.delete(delpos, 0 - d, src);
		}
		else
		{
			long insertpos = pos + orilen;
			int bspos = offset + orilen;
			blocks.overwrite(pos, bs, offset, orilen, src);
			blocks.insert(insertpos, bs, bspos, d, src);
		}
	}

	public long length()
	{
		return m_blocks.newlength;
	}

	public static class BPOverlayRABlocks
	{
		public byte[][] bss;
		public long[] rawoffsets;
		public long[] offsets;
		public int[] lengths;

		public int count;
		public long rawlength;
		public long newlength;

		public byte[] get(int index, BPDataContainerRandomAccess src)
		{
			byte[] rc = bss[index];
			if (rc == null)
			{
				int l = lengths[index];
				rc = new byte[l];
				src.read(rawoffsets[index], rc, 0, l);
				bss[index] = rc;
			}
			return rc;
		}

		protected byte[] getAndCache(int index, BPDataContainerRandomAccess src)
		{
			byte[] rc = bss[index];
			if (rc == null)
			{
				rc = get(index, src);
				bss[index] = rc;
			}
			return rc;
		}

		public void overwrite(long pos, byte[] bs, int offset, int len, BPDataContainerRandomAccess src)
		{
			int starti = findBlockIndex(pos);
			if (starti == -1)
				throw new RuntimeException("index error:" + pos);

			int rlen = len;
			long spos = pos;
			int looplen;
			int loopstart;
			int blen;
			byte[] blockbs;
			for (int i = starti; i < count; i++)
			{
				blockbs = bss[i];
				blen = lengths[i];
				loopstart = (int) (spos - offsets[i]);
				looplen = Math.min(rlen, blen - loopstart);
				if (blockbs == null)
				{
					blockbs = getAndCache(i, src);
					bss[i] = blockbs;
				}
				System.arraycopy(bs, offset, blockbs, loopstart, looplen);
				rlen -= looplen;
				spos += looplen;
				offset += looplen;
				if (rlen <= 0)
					break;
			}
		}

		public void delete(long pos, int len, BPDataContainerRandomAccess src)
		{
			int starti = findBlockIndex(pos);
			if (starti == -1)
				throw new RuntimeException("index error:" + pos);
			long endpos = pos + len - 1;
			int lasti = findBlockIndex(endpos);

			if (starti == lasti)
			{
				deleteInBlock(starti, (int) (pos - offsets[starti]), len, src);
			}
			else
			{
				int posinblock;
				int blen;

				int i = starti;
				int startdeli;
				{
					// firstblock
					posinblock = (int) (pos - offsets[i]);
					if (posinblock == 0)
					{
						startdeli = i;
					}
					else
					{
						startdeli = i + 1;
						blen = lengths[i];
						resizeBlock(i, true, blen - posinblock, src);
					}
				}

				int enddeli;
				{
					i = lasti;
					// lastblock
					posinblock = (int) (endpos - offsets[i]);
					if (posinblock == lengths[i] - 1)
					{
						enddeli = lasti;
					}
					else
					{
						enddeli = lasti - 1;
						blen = lengths[i];
						resizeBlock(i, false, posinblock + 1, src);
					}
				}

				// del blocks
				if (enddeli >= startdeli)
				{
					deleteBlocks(startdeli, enddeli - startdeli + 1);
				}
			}
		}

		protected void deleteInBlock(int index, int pos, int dellen, BPDataContainerRandomAccess src)
		{
			byte[] block = get(index, src);
			int oldlen = lengths[index];
			int nlen = oldlen - dellen;
			byte[] newblock = new byte[nlen];
			int oldpos = 0;

			if (pos > 0)
			{
				System.arraycopy(block, 0, newblock, 0, pos);
				oldpos += pos;
				rawoffsets[index] = rawoffsets[index] + pos;
			}
			oldpos += dellen;
			if (oldpos < oldlen)
				System.arraycopy(block, oldpos, newblock, pos, oldlen - oldpos);
			bss[index] = newblock;
			lengths[index] = newblock.length;
			newlength -= dellen;
		}

		protected void resizeBlock(int index, boolean deltail, int dellen, BPDataContainerRandomAccess src)
		{
			byte[] block = getAndCache(index, src);
			int nlen = lengths[index] - dellen;

			if (deltail)
			{
				lengths[index] = nlen;
			}
			else
			{
				byte[] newblock = new byte[nlen];
				System.arraycopy(block, dellen, newblock, 0, nlen);
				rawoffsets[index] += dellen;
				bss[index] = newblock;
				lengths[index] = nlen;
			}
			newlength -= dellen;
		}

		protected void deleteBlocks(int index, int delcount)
		{
			int oldcount = this.count;
			int newcount = oldcount - delcount;

			byte[][] newbss = new byte[newcount][];
			long[] newrawoffsets = new long[newcount];
			long[] newoffsets = new long[newcount];
			int[] newlengths = new int[newcount];
			long dellen = 0;
			long offset = 0;

			int j = 0;
			for (int i = 0; i < count; i++)
			{
				if (i >= index && i < index + delcount)
				{
					dellen += lengths[i];
					continue;
				}
				else
				{
					newbss[j] = bss[i];
					newrawoffsets[j] = rawoffsets[i];
					newoffsets[j] = offset;
					newlengths[j] = lengths[i];
					j++;
				}
				offset += lengths[i];
			}
			count = newcount;
			bss = newbss;
			rawoffsets = newrawoffsets;
			offsets = newoffsets;
			lengths = newlengths;
			newlength -= dellen;
		}

		public void insert(long pos, byte[] bs, int offset, int len, BPDataContainerRandomAccess src)
		{
			int index = findBlockIndex(pos);
			byte[] block = getAndCache(index, src);
			long blockoffset = offsets[index];
			int blocklen = lengths[index];
			int newlen = blocklen + len;
			int insertpos = (int) (pos - blockoffset);
			byte[] newblock = new byte[newlen];
			if (insertpos > 0)
				System.arraycopy(block, 0, newblock, 0, insertpos);
			System.arraycopy(bs, offset, newblock, insertpos, len);
			if (insertpos < blocklen)
				System.arraycopy(block, insertpos, newblock, insertpos + len, blocklen - insertpos);
			bss[index] = newblock;
			lengths[index] = newlen;
			for (int i = index + 1; i < count; i++)
				offsets[i] += len;
			newlength += len;
		}

		public int findBlockIndex(long pos)
		{
			int count = this.count;
			int si = 0, li = count - 1;
			int p = (si + li) / 2;
			long bstart;
			long bend;
			while (true)
			{
				bstart = offsets[p];
				bend = bstart + lengths[p];
				if (pos < bstart)
				{
					li = p - 1;
					if (li < si)
						break;
					p = (si + li) / 2;
				}
				else if (pos >= bend)
				{
					si = p + 1;
					if (li < si)
						break;
					p = (si + li) / 2;
				}
				else
				{
					return p;
				}
			}
			return -1;
		}

		public void write(OutputStream out, BPDataContainerRandomAccess src) throws IOException
		{
			for (int i = 0; i < count; i++)
				out.write(get(i, src), 0, lengths[i]);
		}
	}

	public void initOverlay()
	{
		if (m_src.canOpen())
		{
			long l = m_src.length();
			int blocksize = m_initblocksize;
			BPOverlayRABlocks blocks = new BPOverlayRABlocks();
			long pos = 0;
			int count = (int) (l / blocksize);
			{
				int p = (int) (l - (blocksize * count));
				if (p > 0)
					count++;
			}
			byte[][] bss = new byte[count][];
			long[] rawoffsets = new long[count];
			long[] offsets = new long[count];
			int[] lengths = new int[count];

			for (int i = 0; i < count; i++)
			{
				rawoffsets[i] = pos;
				offsets[i] = pos;
				lengths[i] = (int) Math.min(l - pos, blocksize);
				pos += blocksize;
			}
			blocks.count = count;
			blocks.bss = bss;
			blocks.rawoffsets = rawoffsets;
			blocks.offsets = offsets;
			blocks.lengths = lengths;
			blocks.newlength = l;
			blocks.rawlength = l;
			m_blocks = blocks;
		}
	}

	public void saveOverlay()
	{
		IOException e = ResourceUtil.useTempResource("temp", "raw", m_blocks.newlength, this::writeResourceWithTemp);
		if (e != null)
			throw new RuntimeException(e);
	}

	protected IOException writeResourceWithTemp(BPResourceIO temp)
	{
		BPOverlayRABlocks blocks = m_blocks;
		BPDataContainerRandomAccess readsrc = m_readsrc;
		BPDataContainerRandomAccess src = readsrc != null ? readsrc : m_src;
		IOException e = temp.useOutputStream(out ->
		{
			try
			{
				blocks.write(out, src);
				return null;
			}
			catch (IOException e2)
			{
				return e2;
			}
		});
		if (e != null)
		{
			return e;
		}
		if (temp.isFileSystem())
		{

		}
		else
		{
			writeAll(IOUtil.read(temp));
		}

		return null;
	}

	public void clearOverlay()
	{
		m_blocks = null;
		m_readsrc = null;
	}

	public void copyFrom(BPDataContainerRandomAccessOverlay other)
	{
		this.m_blocks = other.getBlocks();
		this.m_readsrc = other.getSource();
	}
}