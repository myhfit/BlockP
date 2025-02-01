package bp.data;

import bp.res.BPResource;

public interface BPStructureAnalyzer<T> extends BPAnalyzer<T>
{
	BPResource[] getStructure();
	
	BPResource[] getStructureWithAnalyzed(T data);
}
