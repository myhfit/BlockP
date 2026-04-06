package bp.nativehelper;

public interface BPNativeHelper
{
	boolean checkPlatform();

	String getName();

	<INTF> INTF getInterface(String name);

	void register(String name, Object intf);

	void unregister(String name);
}
