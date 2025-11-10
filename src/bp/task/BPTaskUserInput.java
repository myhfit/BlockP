package bp.task;

import java.util.Map;

import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.ClassUtil;
import bp.util.JSONUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPTaskUserInput extends BPTaskLocal<Object>
{
	public final static String S_VTYPE_SV = "Simple Value";
	public final static String S_VTYPE_JSON = "JSON";

	public BPTaskUserInput()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "User Input";
	}

	protected void doStart()
	{
		Object[] ps = (Object[]) m_params;
		String vtype = (String) ps[0];
		String vcast = ps.length > 1 ? TextUtil.eds((String) ps[1]) : null;
		String vdefault = ps.length > 2 ? TextUtil.eds((String) ps[2]) : null;

		setStarted();
		String s = Std.prompt("Input Data(s)");
		if (s == null && vdefault == null)
		{
			m_future.completeExceptionally(new RuntimeException("User Canceled"));
			setFailed(new RuntimeException("User Canceled"));
		}
		else
		{
			Object v = s;
			if (v == null)
				v = vdefault;
			if (S_VTYPE_SV.equalsIgnoreCase(vtype))
			{
				if (vcast != null)
				{
					Class<?> cls = ClassUtil.getTClass(vcast);
					v = ClassUtil.callMethod(cls, "valueOf", new Class[] { String.class }, null, false, v);
				}
			}
			else if (S_VTYPE_JSON.equalsIgnoreCase(vtype))
			{
				v = JSONUtil.decode((String) v);
			}
			m_future.complete(v);
			setCompleted();
		}
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		Object[] ps = (Object[]) m_params;
		if (ps != null && ps.length > 0)
		{
			String vtype = (String) ps[0];
			String vcast = ps.length > 1 ? TextUtil.eds((String) ps[1]) : null;
			String vdefault = ps.length > 2 ? TextUtil.eds((String) ps[2]) : null;

			rc.put("vtype", vtype);
			rc.put("vcast", vcast);
			rc.put("vdefault", vdefault);
		}
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		String vtype = (String) data.get("vtype");
		String vcast = (String) data.get("vcast");
		String vdefault = (String) data.get("vdefault");

		m_params = new Object[] { vtype, vcast, vdefault };
	}

	public static class BPTaskFactoryUserInput extends BPTaskFactoryBase<BPTaskUserInput>
	{
		public String getName()
		{
			return "User Input";
		}

		protected BPTaskUserInput createTask()
		{
			return new BPTaskUserInput();
		}

		public Class<? extends BPTask<?>> getTaskClass()
		{
			return BPTaskUserInput.class;
		}
	}
}
