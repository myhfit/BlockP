package bp.id;

import java.util.UUID;

public class UUIDGenerator implements IDGenerator
{
	public String genID()
	{
		return UUID.randomUUID().toString();
	}
}
