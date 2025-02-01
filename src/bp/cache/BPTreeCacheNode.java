package bp.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BPTreeCacheNode<T>
{
	protected volatile List<BPTreeCacheNode<?>> m_children;

	protected String m_key;
	protected T m_value;

	protected volatile boolean m_valid = true;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <TC> List<BPTreeCacheNode<TC>> getChildren()
	{
		return (List) m_children;
	}

	public void addChild(BPTreeCacheNode<?> child)
	{
		if (m_children == null)
		{
			m_children = new ArrayList<BPTreeCacheNode<?>>();
		}
		m_children.add(child);
	}

	public void setKey(String key)
	{
		m_key = key;
	}

	public void setValue(T value)
	{
		m_value = value;
	}

	public String getKey()
	{
		return m_key;
	}

	public T getValue()
	{
		return m_value;
	}

	public boolean isValid()
	{
		return m_valid;
	}

	public void setValid(boolean flag)
	{
		m_valid = flag;
	}

	public int count()
	{
		int c = 1;
		if (m_children != null)
		{
			for (BPTreeCacheNode<?> chd : m_children)
			{
				c += chd.count();
			}
		}
		return c;
	}

	public List<BPTreeCacheNode<?>> filter(Predicate<BPTreeCacheNode<?>> filterfunc, List<BPTreeCacheNode<?>> result)
	{
		List<BPTreeCacheNode<?>> rc = result;
		if (rc == null)
			rc = new ArrayList<BPTreeCacheNode<?>>();
		if (filterfunc.test(this))
		{
			rc.add(this);
		}
		List<BPTreeCacheNode<?>> children = m_children;
		if (children != null)
		{
			for (BPTreeCacheNode<?> child : children)
			{
				child.filter(filterfunc, rc);
			}
		}
		return rc;
	}

	public void clear()
	{
		List<BPTreeCacheNode<?>> children = m_children;
		if (children != null)
			children.clear();
	}

	public static class BPTreeCacheNodeRoot<T> extends BPTreeCacheNode<T>
	{
		protected volatile boolean m_rootvalid = true;

		public boolean isRootValid()
		{
			return m_rootvalid;
		}

		public void setRootValid(boolean flag)
		{
			m_rootvalid = flag;
		}
	}
}
