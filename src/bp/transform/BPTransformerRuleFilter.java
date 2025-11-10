package bp.transform;

import java.util.Collection;
import java.util.Map;

public abstract class BPTransformerRuleFilter<DATA> extends BPTransformerBase<Collection<DATA>>
{
	protected String m_rule;

	public void setRule(String rule)
	{
		m_rule = rule;
	}

	public String getRule()
	{
		return m_rule;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		rc.put("rule", m_rule);
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		m_rule = (String) data.get("rule");
	}
}
