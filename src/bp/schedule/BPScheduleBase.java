package bp.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import bp.schedule.BPScheduleTarget.BPScheduleTargetParams;
import bp.util.ClassUtil;
import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.Std;

public abstract class BPScheduleBase implements BPSchedule
{
	protected volatile String m_name;
	protected volatile BPScheduleTarget m_target;
	protected boolean m_blocktarget;
	protected volatile String m_targetfac;
	protected volatile String m_targetparam;
	protected volatile boolean m_enabled;

	protected BPScheduleBase()
	{
		m_enabled = true;
	}

	public String getName()
	{
		return m_name;
	}

	public boolean isEnabled()
	{
		return m_enabled;
	}

	public void setEnabled(boolean flag)
	{
		m_enabled = flag;
	}

	protected final void runInner(long ct, BPScheduleTargetParams params)
	{
		m_target.accept(ct, params);
	}

	public void setMappedData(Map<String, Object> data)
	{
		m_name = (String) data.get("name");
		m_enabled = ObjUtil.toBool(data.get("enabled"), true);
		String targetfac = (String) data.get("targetfac");
		String targetparams = (String) data.get("targetparams");
		setTarget(targetfac, targetparams);
	}

	protected void setTarget(String targetfac, String targetparams)
	{
		if (!m_blocktarget)
		{
			m_targetfac = targetfac;
			m_targetparam = targetparams;
			if (m_targetfac != null)
				setTarget(createTarget(targetfac, targetparams));
		}
	}

	protected final static BPScheduleTarget createTarget(String targetfac, String targetparams)
	{
		try
		{
			ServiceLoader<BPScheduleTargetFactory> facs = ClassUtil.getServices(BPScheduleTargetFactory.class);
			for (BPScheduleTargetFactory fac : facs)
			{
				if (fac.getName().equals(targetfac))
				{
					Map<String, Object> ps = null;
					if (targetparams != null)
						ps = JSONUtil.decode(targetparams);
					return fac.create(ps);
				}
			}
		}
		catch (Error e)
		{
			Std.err(e);
		}
		return null;
	}

	protected void setTarget(BPScheduleTarget target)
	{
		if (!m_blocktarget)
			m_target = target;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_name);
		rc.put("enabled", m_enabled);
		rc.put("targetfac", m_targetfac);
		rc.put("targetparams", m_targetparam);
		return rc;
	}
}
