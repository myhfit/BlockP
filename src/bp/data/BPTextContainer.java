package bp.data;

public interface BPTextContainer extends BPDataContainer
{
	String readAllText();

	boolean writeAllText(String text);

	void setEncoding(String encoding);
	
	String getEncoding();
}
