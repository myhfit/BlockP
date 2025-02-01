package bp.data;

public interface BPMContainer<D extends BPMData> extends BPDataContainer
{
	D readMData(boolean loadsub);

	Boolean writeMData(D data, boolean savesub);
}
