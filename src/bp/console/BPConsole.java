package bp.console;

public interface BPConsole
{
	String getName();

	boolean isStringMode();

	BPConsoleController getController();

	boolean start();

	boolean stop();

	void setNotify(Runnable cb);

	// byte[] dlBytes();

	String dlString();

	void setEncoding(String encoding);

	String getEncoding();
}
