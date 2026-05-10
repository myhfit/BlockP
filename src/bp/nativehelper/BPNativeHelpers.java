package bp.nativehelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.util.ClassUtil;
import bp.util.Std;

public class BPNativeHelpers
{
	public static List<BPNativeHelper> S_HS;

	private static boolean S_JNA;

	static
	{
		INIT();
	}

	private final static void INIT()
	{
		S_JNA = ClassUtil.getTClass("com.sun.jna.Native", ClassUtil.getExtensionClassLoader()) != null;
		Std.debug("JNA Support:" + S_JNA);
		S_HS = new CopyOnWriteArrayList<>();
		S_HS.add(new BPNativeHelperJNI());
		{
			BPNativeHelperFFM ffm = new BPNativeHelperFFM();
			if (ffm.checkPlatform())
				S_HS.add(ffm);
		}
	}

	public final static boolean hasJNASupport()
	{
		return S_JNA;
	}

	public final static <INTF> INTF getInterface(String name)
	{
		List<BPNativeHelper> hs = new ArrayList<BPNativeHelper>(S_HS);
		for (BPNativeHelper h : hs)
		{
			INTF intf = h.getInterface(name);
			if (intf != null)
				return intf;
		}
		return null;
	}

	public final static List<BPNativeHelper> getHelpers()
	{
		return new ArrayList<BPNativeHelper>(S_HS);
	}

	@SuppressWarnings("unchecked")
	public final static <H extends BPNativeHelper> H getHelper(String name)
	{
		List<BPNativeHelper> hs = new ArrayList<BPNativeHelper>(S_HS);
		for (BPNativeHelper h : hs)
		{
			if (name.equals(h.getName()))
				return (H) h;
		}
		return null;
	}

	public final static void register(BPNativeHelper helper)
	{
		String name = helper.getName();
		int vi = -1;
		for (int i = 0; i < S_HS.size(); i++)
		{
			if (name.equals(S_HS.get(i).getName()))
			{
				vi = i;
				break;
			}
		}
		if (vi > -1)
			S_HS.set(vi, helper);
		else
			S_HS.add(helper);
	}
}