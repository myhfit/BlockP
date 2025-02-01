package bp.data;

import java.util.function.Supplier;

public class BPCommandResult
{
	public boolean success;
	public Object data;

	public final static BPCommandResult RUN(Supplier<?> seg)
	{
		BPCommandResult rc = new BPCommandResult();
		try
		{
			rc.data = seg.get();
			rc.success = true;
		}
		catch (Exception e)
		{
			rc.data = e;
		}
		return rc;
	}

	public final static BPCommandResult RUN_B(Supplier<Boolean> seg)
	{
		boolean success = false;
		BPCommandResult rc = new BPCommandResult();
		try
		{
			success = seg.get();
		}
		catch (Exception e)
		{
			rc.data = e;
		}
		rc.success = success;
		return rc;
	}

	public final static BPCommandResult OK(Object r)
	{
		BPCommandResult rc = new BPCommandResult();
		rc.success = true;
		rc.data = r;
		return rc;
	}

	public final static BPCommandResult FAIL(Object r)
	{
		BPCommandResult rc = new BPCommandResult();
		rc.success = false;
		rc.data = r;
		return rc;
	}

	public final static BPCommandResult ERR(Exception r)
	{
		BPCommandResult rc = new BPCommandResult();
		rc.success = false;
		rc.data = r;
		return rc;
	}
}
