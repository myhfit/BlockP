package bp.data;

import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.BiConsumer;

public interface BPXYDData extends BPXYData, Closeable
{
	void setDataListener(WeakReference<BiConsumer<List<BPXData>, Integer>> insertcallback, WeakReference<BiConsumer<Integer, Integer>> removecallback, WeakReference<Runnable> endcallback);

	void clearDataListeners();

	void insertPage(List<BPXData> page, Integer pos);

	default void appendPage(List<BPXData> page)
	{
		insertPage(page, null);
	}

	void removePage(int start, int end);

	void complete();
}