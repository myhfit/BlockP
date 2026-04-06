package bp.nativehelper;

import bp.util.ClassUtil;

public class BPNativeHelperFFM extends BPNativeHelperBase
{
	public final static String HELPER_FFM = "ffm";

	public boolean checkPlatform()
	{
		try
		{
			if (ClassUtil.tryCallSimpleMethod("java.lang.foreign.Linker", "nativeLinker", null) != null)
				if (ClassUtil.getTClass("java.lang.foreign.Arena") != null)
					return true;
		}
		catch (Exception e)
		{

		}
		return false;
	}

	public String getName()
	{
		return HELPER_FFM;
	}
}