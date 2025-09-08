package bp.data;

public interface BPDataContainerRandomAccess extends BPDataContainer
{
	public final static int RAWIO_BLOCKSIZE_DEFAULT = 4096;

	int read(long pos, byte[] bs, int offset, int len);

	void overwrite(long pos, byte[] bs, int offset, int len);

	default void replace(long pos, byte[] bs, int offset, int orilen, int len)
	{
		throw new RuntimeException("not supported");
	}

	long length();
}
