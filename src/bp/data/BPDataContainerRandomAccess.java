package bp.data;

public interface BPDataContainerRandomAccess extends BPDataContainer
{
	int read(long pos, byte[] bs, int offset, int len);

	void write(long pos, byte[] bs, int offset, int len);
	
	long length();
}
