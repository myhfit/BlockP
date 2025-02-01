package bp.task;

import java.util.concurrent.Future;

import bp.data.BPSLData;

public interface BPTask<V> extends BPSLData
{
	Future<V> getFuture();

	float getProgress();

	String getProgressText();

	String start();

	String getID();

	boolean isRunning();

	void stop();

	void setParams(Object params);

	String getTaskName();

	String getName();

	String getStatus();

	void setManagerFlag(int mf);

	int getManagerFlag();

	default String getCategory()
	{
		return null;
	}

	default boolean isNoSave()
	{
		return false;
	}

	boolean isAutoRemove();

}
