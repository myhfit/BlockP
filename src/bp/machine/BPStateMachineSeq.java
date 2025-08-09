package bp.machine;

public abstract class BPStateMachineSeq<E, C, SRC> extends BPStateMachineBase<E, C, SRC>
{
	protected abstract E getElement(SRC src, long pos);

	public void run()
	{
		SRC src = m_src;
		BPStateBase<E, C> cur = (BPStateBase<E, C>) m_cur;
		C context = m_context;
		while (cur != null)
		{
			E ele = getElement(src, cur.pos);
			if (ele != null)
				cur = cur.input(getElement(src, cur.pos), context);
			else
			{
				cur.end(context);
				break;
			}
		}
	}

	public C context()
	{
		return m_context;
	}

	public void setContext(C context)
	{
		m_context = context;
	}

	public static abstract class BPStateBase<E, C> implements BPState<E, C>
	{
		public long pos;
	}
}
