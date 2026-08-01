package bp.data;

import static bp.data.BPXYData.XYDataUtil.cloneDatas;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import bp.util.LockUtil;
import bp.util.LogicUtil;

public class BPXYDDataBase implements BPXYDData
{
	protected volatile List<BPXData> m_datas;
	protected volatile Class<?>[] m_ccs;
	protected volatile String[] m_cns;
	protected volatile String[] m_cls;

	protected volatile WeakReference<Consumer<BPXYDData>> m_pcallback;
	protected volatile WeakReference<BiConsumer<List<BPXData>, Integer>> m_insertcallback;
	protected volatile WeakReference<BiConsumer<Integer, Integer>> m_removecallback;
	protected volatile WeakReference<Runnable> m_endcallback;

	protected volatile ReadWriteLock m_lock = new ReentrantReadWriteLock();
	protected volatile Future<Boolean> m_completefuture;

	public List<BPXData> getDatas()
	{
		return m_datas;
	}

	public Class<?>[] getColumnClasses()
	{
		return m_ccs;
	}

	public String[] getColumnNames()
	{
		return m_cns;
	}

	public String[] getColumnLabels()
	{
		return m_cls;
	}

	public void setDatas(List<BPXData> datas)
	{
		m_datas = datas;
	}

	public void setColumnClasses(Class<?>[] colclasses)
	{
		m_ccs = colclasses;
	}

	public void setColumnNames(String[] colnames)
	{
		m_cns = colnames;
	}

	public void setColumnLabels(String[] collabels)
	{
		m_cls = collabels;
	}

	public void setDataListener(WeakReference<BiConsumer<List<BPXData>, Integer>> insertcallback, WeakReference<BiConsumer<Integer, Integer>> removecallback, WeakReference<Runnable> endcallback)
	{
		m_insertcallback = insertcallback;
		m_removecallback = removecallback;
		m_endcallback = endcallback;
	}

	public void clearDataListeners()
	{
		m_insertcallback = null;
		m_removecallback = null;
		m_endcallback = null;
	}

	public void close()
	{
		m_pcallback = null;
		m_insertcallback = null;
		m_removecallback = null;
		m_endcallback = null;
		if (m_datas != null)
		{
			m_datas.clear();
			m_datas = null;
		}

		m_ccs = null;
		m_cns = null;
		m_cls = null;
	}

	public void insertPage(List<BPXData> page, Integer pos)
	{
		BiConsumer<List<BPXData>, Integer> insertcallback = LogicUtil.unwrapReference(m_insertcallback);
		if (insertcallback != null)
			LockUtil.rwLock(m_lock, true, () -> insertcallback.accept(page, null));
	}

	public void removePage(int start, int end)
	{
		BiConsumer<Integer, Integer> removecallback = LogicUtil.unwrapReference(m_removecallback);
		if (removecallback != null)
			LockUtil.rwLock(m_lock, true, () -> removecallback.accept(start, end));
	}

	public void complete()
	{
		Runnable endcallback = LogicUtil.unwrapReference(m_endcallback);
		if (endcallback != null)
			LockUtil.rwLock(m_lock, true, () -> endcallback.run());
	}

	public BPXYDData clone()
	{
		Class<?>[] ccs = new Class<?>[m_ccs.length];
		String[] cns = new String[m_cns.length];
		System.arraycopy(m_ccs, 0, ccs, 0, m_ccs.length);
		System.arraycopy(m_cns, 0, cns, 0, m_cns.length);
		String[] cls = null;
		if (m_cls != null)
		{
			cls = new String[m_cls.length];
			System.arraycopy(m_cls, 0, cls, 0, m_cls.length);
		}
		BPXYDDataBase rc = new BPXYDDataBase();
		rc.setColumnClasses(ccs);
		rc.setColumnNames(cns);
		rc.setColumnLabels(cls);
		rc.setDatas(cloneDatas(m_datas));
		return rc;
	}
}
