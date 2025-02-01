package bp.parser;

import java.util.List;

public interface BPParserTreeNode<D>
{
	List<BPParserTreeNode<D>> getChildren();

	BPParserTreeNode<D> getParent();

	void clearResources();

	D getData();
}
