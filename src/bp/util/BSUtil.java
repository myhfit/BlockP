package bp.util;

public class BSUtil
{
	public final static byte[] reductBlockFromHead(byte[] bs, int orilen, int dellen)
	{
		int nlen = orilen - dellen;
		byte[] newblock = new byte[nlen];
		System.arraycopy(bs, dellen, newblock, 0, nlen);
		return newblock;
	}

	public final static byte[] newBS(byte[] bs, int offset, int len)
	{
		byte[] rc = new byte[len];
		System.arraycopy(bs, offset, rc, 0, len);
		return rc;
	}
}
