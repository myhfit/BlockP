package bp.util;

public class ErrUtil
{
	public final static Throwable getRootError(Throwable e)
	{
		int max = 10;
		Throwable cur = e;
		int l = 0;
		while (l < max)
		{
			Throwable par = cur.getCause();
			if (par == null || par == cur)
				break;
			cur = par;
			l++;
		}
		return cur;
	}

}
