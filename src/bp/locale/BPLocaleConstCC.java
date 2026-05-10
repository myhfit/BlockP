package bp.locale;

//Computer Common Dict
public enum BPLocaleConstCC implements BPLocaleConstDirect
{
	TEXT,
	BYTEARR("byte[]"),
	ERR("Error"),
	KEY,
	VALUE,
	CLASS,
	FILE,
	DIR("Directory"),
	RES("Resource"),
	NAME,
	FULLNAME("Full Name"),
	CODE,
	USER,
	USERNAME,
	ACCOUNT,
	PASSWORD,
	ATTRIB,
	CREATION_TIME("Creation Time"),
	LAST_MODIFIED("Last Modified"),
	LAST_ACCESS("Last Access"),
	COUNT,
	SIZE,
	TYPE,
	PATH,
	SYS,
	SOURCE,
	TARGET,
	DESTINATION,
	FUNCTION,
	METHOD,
	FILENAME,
	CLASSNAME,
	TASK,
	INPUT,
	OUTPUT,
	FONT,
	SUCCESS,
	FAILED,
	FILTER,
	START,
	STARTED,
	DONE,
	ENDED,
	STATUS,
	PROGRESS,
	SCHEDULER,
	PARAMETERS,
	ENCODING,
	VERSION,
	;

	public final static String PACK_COMPUTER_COMMON = "c_c";

	private String m_value;

	public String getPackName()
	{
		return PACK_COMPUTER_COMMON;
	}

	private BPLocaleConstCC()
	{
	}

	private BPLocaleConstCC(String v)
	{
		m_value = v;
	}

	public String getValue(int flag)
	{
		return m_value == null ? getNormalName() : m_value;
	}
}
