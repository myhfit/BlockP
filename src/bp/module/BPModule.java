package bp.module;

public interface BPModule
{
	String getName();
	
	String getModuleName();

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

	default void transferRootData(BPModule oldmodule)
	{

	}

	default void initRootData()
	{

	}

	void setNamePrefix(String prefix);
}
