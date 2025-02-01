package bp.data;

import java.util.concurrent.Future;

public interface BPAnalyzer<T>
{
	boolean analyze(T data);

	Future<Boolean> analyzeAsync(T data);

	void setOption(String key, Object value);
	
	boolean hasAnalyzed();
}
