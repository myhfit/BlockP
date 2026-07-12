package bp.res;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.data.BPMData;
import bp.typeext.Nameable;

public interface BPResource extends BPMData, Nameable
{
	String getExt();

	String getName();

	String getResType();

	BPResource getParentResource();

	boolean isFileSystem();

	boolean isLeaf();

	default boolean isIO()
	{
		return false;
	}

	default boolean isRoot()
	{
		return false;
	}

	default boolean isRoutable()
	{
		return false;
	}

	boolean isReadOnly();

	boolean needNetwork();

	boolean isWeb();

	boolean isLocal();

	boolean delete();

	default boolean canOpen()
	{
		return false;
	}

	void release();

	String getID();

	default BPResource[] listResources()
	{
		return listResources(false);
	}

	BPResource[] listResources(boolean isdelta);

	boolean isProjectResource();

	default boolean isVirtual()
	{
		return false;
	}
	
	default boolean isFactory()
	{
		return false;
	}

	default boolean isOverlay()
	{
		return false;
	}

	default boolean isTemp()
	{
		return false;
	}

	default boolean openWithTempID()
	{
		return false;
	}

	default boolean fullHandleAction()
	{
		return false;
	}

	default BPSetting getSetting()
	{
		return null;
	}

	default void setSetting(BPConfig config)
	{

	}

	default <T> T getMeta(String key)
	{
		return null;
	}

	boolean rename(String newname);

	String getURI();
}
