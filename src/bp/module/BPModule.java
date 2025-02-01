package bp.module;

public interface BPModule
{
	String getName();

	int getVersion();

	Object createRootInstance();

	<ROOT> ROOT getRoot();

	void setLoadTime(long t);

	long getLoadTime();

	default boolean initRoot(Object root)
	{
		return true;
	}

	default boolean test()
	{
		return true;
	}

	default void unload()
	{

	}

	default void transferRootData(BPModule newmodule)
	{

	}

	default void initRootData()
	{

	}
}
