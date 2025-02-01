package bp.res;

import java.util.List;

public interface BPResourceParent extends BPResource
{
	void addChild(BPResource res);

	void removeChild(BPResource res);

	void removeAll(List<BPResource> res);
}
