package bp.data;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class BPDataTreeNode<T, C extends BPDataTreeNode<?, ?>>
{
	public boolean isroot;
	public T data;
	public String name;
	public List<C> children;
	public C parent;

	public String toString()
	{
		return name == null ? super.toString() : name;
	}

	@SuppressWarnings("unchecked")
	public <N extends BPDataTreeNode<?, ?>> List<N> getChildren()
	{
		return (List<N>) children;
	}
	
	@SuppressWarnings("unchecked")
	public <N extends BPDataTreeNode<?, ?>> N find(Predicate<BPDataTreeNode<?, ?>>... testfuncs)
	{
		LinkedList<Predicate<? extends BPDataTreeNode<?, ?>>> q = new LinkedList<Predicate<? extends BPDataTreeNode<?, ?>>>();
		for (Predicate<BPDataTreeNode<?, ?>> f : testfuncs)
			q.add(f);
		return find(q);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <N extends BPDataTreeNode<?, ?>> N find(Deque<Predicate<? extends BPDataTreeNode<?, ?>>> testfuncs)
	{
		if (testfuncs.size() == 0 || testfuncs == null)
			return (N) this;
		if (children == null)
			return null;
		N rc = null;
		Predicate f = testfuncs.pop();
		for (C chd : children)
		{
			if (f.test(chd))
			{
				rc = chd.find(testfuncs);
				if (rc != null)
					break;
			}
		}
		testfuncs.addFirst(f);
		return rc;
	}

	public static class BPDataTreeNodeMO extends BPDataTreeNode<Map<String, Object>, BPDataTreeNode<?, ?>>
	{

	}
}
