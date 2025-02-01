package bp.parser;

import java.util.List;

public class BPParserTreeNodeBase<D> implements BPParserTreeNode<D>
{
	public List<BPParserTreeNode<D>> children;
	public BPParserTreeNode<D> parent;
	public D data;

	public List<BPParserTreeNode<D>> getChildren()
	{
		return children;
	}

	public void setChildren(List<BPParserTreeNode<D>> children)
	{
		this.children = children;
	}

	public BPParserTreeNode<D> getParent()
	{
		return parent;
	}

	public void setParent(BPParserTreeNode<D> parent)
	{
		this.parent = parent;
	}

	public void clearResources()
	{
		if (children != null)
		{
			for (BPParserTreeNode<D> child : children)
			{
				child.clearResources();
			}
			children = null;
		}
		parent = null;
	}

	public D getData()
	{
		return data;
	}

	public final static boolean eqLeft(char[] src, char[] tar, int pos)
	{
		boolean rc = true;
		int l = tar.length;
		for (int i = 0; i < l; i++)
		{
			char ctar = tar[l - i + 1];
			char csrc = src[pos - i];
			if (ctar != csrc)
			{
				rc = false;
				break;
			}
		}
		return rc;
	}
}
