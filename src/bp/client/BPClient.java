package bp.client;

public interface BPClient
{
	<T> T call(String action, Object... params);
}
