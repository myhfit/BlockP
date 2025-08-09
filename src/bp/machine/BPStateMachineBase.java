package bp.machine;

public abstract class BPStateMachineBase<E, C, SRC> implements BPStateMachine<E, C, SRC>
{
	protected volatile BPState<E, C> m_cur;
	protected volatile C m_context;
	protected volatile SRC m_src;

	public void bind(SRC src)
	{
		m_src = src;
	}

	public BPState<E, C> current()
	{
		return m_cur;
	}

	public void setCurrent(BPState<E, C> cur)
	{
		m_cur = cur;
	}

	public C context()
	{
		return m_context;
	}
}
