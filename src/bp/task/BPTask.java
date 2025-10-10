package bp.task;

import java.util.concurrent.Future;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.data.BPSLData;

public interface BPTask<V> extends BPSLData
{
	Future<V> getFuture();

	float getProgress();

	String getProgressText();

	String start();

	String getID();

	void setID(String id);

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

	default boolean needConfirm()
	{
		return false;
	}

	default BPSetting getSetting()
	{
		return null;
	}

	default void setSetting(BPConfig setting)
	{

	}

	void mergeDynamicParams(Object params);

	void clearDynamicParams();
}
