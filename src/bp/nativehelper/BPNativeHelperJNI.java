package bp.nativehelper;

public class BPNativeHelperJNI extends BPNativeHelperBase
{
	public final static String HELPER_JNI = "jni";

	public boolean checkPlatform()
	{
		return true;
	}

	public String getName()
	{
		return HELPER_JNI;
	}
}