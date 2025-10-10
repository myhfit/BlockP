package bp.util;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.LinkedHashMap;
import java.util.Map;

public class OSInfoHandlers
{
	public final static Map<String, Object> getOSInfo()
	{
		Map<String, Object> rc = new LinkedHashMap<String, Object>();
		OperatingSystemMXBean osbean = ManagementFactory.getOperatingSystemMXBean();
		if (osbean instanceof com.sun.management.OperatingSystemMXBean)
		{
			com.sun.management.OperatingSystemMXBean osb2 = (com.sun.management.OperatingSystemMXBean) osbean;
			rc.put("OS", osb2.getName());
			rc.put("OS Arch", osb2.getArch());
			rc.put("OS Version", osb2.getVersion());
			rc.put("Physical Memory", NumberUtil.formatByteCount(osb2.getTotalPhysicalMemorySize()));
			rc.put("Free Physical Memory", NumberUtil.formatByteCount(osb2.getFreePhysicalMemorySize()));
			rc.put("Swap Space", NumberUtil.formatByteCount(osb2.getTotalSwapSpaceSize()));
			rc.put("Free Swap Space", NumberUtil.formatByteCount(osb2.getFreeSwapSpaceSize()));
		}
		return rc;
	}
}
