package bp.data;

import java.io.Closeable;

public interface BPDataSource extends Closeable, BPMData
{
	BPDataSourceType getDSType();

	public enum BPDataSourceType
	{
		FILE, STREAM, JDBC
	}
}