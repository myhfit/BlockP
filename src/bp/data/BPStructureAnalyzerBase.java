package bp.data;

import bp.res.BPResource;
import bp.util.LockUtil;

public abstract class BPStructureAnalyzerBase<T> extends BPAnalyzerBase<T> implements BPStructureAnalyzer<T>
{
	public BPResource[] getStructureWithAnalyzed(T data)
	{
		if (m_analyzed.get())
			return getStructure();
		return LockUtil.rwLock(m_lock, true, () ->
		{
			if (!m_analyzed.get())
			{
				analyze(data);
				return getStructure();
			}
			return getStructure();
		});
	}
}
