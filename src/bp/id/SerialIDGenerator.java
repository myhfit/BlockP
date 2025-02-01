package bp.id;

import java.util.concurrent.atomic.AtomicLong;

public class SerialIDGenerator implements IDGenerator
{
	private final AtomicLong m_value = new AtomicLong(1);

	public String genID()
	{
		return Long.toString(m_value.getAndIncrement());
	}

	public void setValue(long v)
	{
		m_value.set(v);
	}
}
