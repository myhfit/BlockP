package bp.os;

public final class BPOSFunctions
{
	@FunctionalInterface
	public static interface RUN_SIMPLE
	{
		int run(String cmd, String workdir, String[] args);
	}
}
