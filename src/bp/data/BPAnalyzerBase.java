package bp.data;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class BPAnalyzerBase<T> implements BPAnalyzer<T>
{
	protected volatile Map<String, Object> m_options = new ConcurrentHashMap<String, Object>(0);
	protected final AtomicBoolean m_analyzed = new AtomicBoolean(false);
	protected final ReadWriteLock m_lock=new ReentrantReadWriteLock();

	public Future<Boolean> analyzeAsync(T data)
	{
		return CompletableFuture.supplyAsync(() -> analyze(data));
	}

	public void setOption(String key, Object value)
	{
		m_options.put(key, value);
	}

	public boolean hasAnalyzed()
	{
		return m_analyzed.get();
	}
}
