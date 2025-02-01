package bp.script;

import bp.data.BPMData;

public interface BPScript extends BPMData
{
	String getScriptText();

	String getName();

	String getLanguage();
	
	BPScript clone();
}
