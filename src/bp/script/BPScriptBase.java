package bp.script;

import java.util.HashMap;
import java.util.Map;

public class BPScriptBase implements BPScript
{
	protected volatile String m_language;
	protected volatile String m_name;
	protected volatile String m_text;

	public String getName()
	{
		return m_name;
	}

	public String getLanguage()
	{
		return m_language;
	}

	public String getScriptText()
	{
		return m_text;
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public void setLanguage(String language)
	{
		m_language = language;
	}

	public void setScriptText(String text)
	{
		m_text = text;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_name);
		rc.put("language", m_language);
		rc.put("scripttext", m_text);
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		m_name = (String) data.get("name");
		m_language = (String) data.get("language");
		m_text = (String) data.get("scripttext");
	}

	public BPScript clone()
	{
		BPScriptBase rc = new BPScriptBase();
		rc.setMappedData(getMappedData());
		return rc;
	}
}
