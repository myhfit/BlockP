package bp.typeext;

import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public interface Traversable
{
	Iterable<Traversable> getChildren();

	default boolean visitSelf()
	{
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	default <C> boolean traverseFind(BiPredicate<? extends Traversable, C> cb, C context)
	{
		if (visitSelf())
		{
			if (!((BiPredicate) cb).test(this, context))
				return false;
		}
		Iterable<Traversable> chds = getChildren();
		if (chds == null)
			return false;
		return traverseFind(chds, cb, context, new HashSet<Traversable>());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	default <C> void traverse(BiConsumer<? extends Traversable, C> cb, C context)
	{
		if (visitSelf())
			((BiConsumer) cb).accept(this, context);
		Iterable<Traversable> chds = getChildren();
		if (chds == null)
			return;
		traverse(chds, cb, context, new HashSet<Traversable>());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	default <C> boolean traverseFind(Iterable<Traversable> nodes, BiPredicate<? extends Traversable, C> cb, C context, HashSet<Traversable> checker)
	{
		for (Traversable node : nodes)
		{
			if (checker.contains(node))
				continue;
			else
				checker.add(node);
			if (((BiPredicate) cb).test(node, context))
				return true;
			Iterable<Traversable> chds = node.getChildren();
			if (chds != null)
				if (traverseFind(chds, cb, context, checker))
					return true;
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	default <C> void traverse(Iterable<Traversable> nodes, BiConsumer<? extends Traversable, C> cb, C context, HashSet<Traversable> checker)
	{
		for (Traversable node : nodes)
		{
			if (checker.contains(node))
				continue;
			else
				checker.add(node);
			((BiConsumer) cb).accept(node, context);
			Iterable<Traversable> chds = node.getChildren();
			if (chds != null)
				traverse(chds, cb, context, checker);
		}
	}
}
