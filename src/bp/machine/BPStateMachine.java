package bp.machine;

public interface BPStateMachine<E, C, SRC>
{
	BPState<E, C> current();

	C context();

	void bind(SRC src);

	void run();

	SRC getSource();

	public static interface BPState<E, C>
	{
		<T extends BPState<E, C>> T input(E e, C context, BPStateMachine<E, C, ?> machine);

		void end(C context);
	}
}
