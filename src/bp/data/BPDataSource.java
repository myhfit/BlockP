package bp.data;

import java.io.Closeable;
import java.util.List;
import java.util.function.Function;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.res.BPResourceDataSource;

public interface BPDataSource extends Closeable, BPMData
{
	BPDataSourceType getDSType();

	@SuppressWarnings("unchecked")
	default <T> T useStructure(Function<? extends BPResourceDataSource, T> seg)
	{
		BPResourceDataSource res = getStructureResource();
		if (res == null)
			return null;
		return ((Function<BPResourceDataSource, T>) seg).apply(res);
	}

	default BPSetting getSetting()
	{
		return null;
	}

	default void setSetting(BPConfig config)
	{

	}

	default BPResourceDataSource getStructureResource()
	{
		return null;
	}

	default <C, T> T useData(Function<C, T> seg)
	{
		return null;
	}

	default <C, T> T getData(C context, Object... path)
	{
		return null;
	}

	default <C> List<Object[]> getDataPaths(C context)
	{
		return null;
	}

	public enum BPDataSourceType
	{
		FILE, STREAM, JDBC
	}
}