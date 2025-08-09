package bp.machine;

public interface BPStateMachine<E, C, SRC>
{
	BPState<E, C> current();

	C context();

	void bind(SRC src);

	void run();

	public static interface BPState<E, C>
	{
		<T extends BPState<E, C>> T input(E e, C context);

		void end(C context);
	}
}
