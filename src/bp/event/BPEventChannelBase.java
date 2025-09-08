package bp.event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class BPEventChannelBase implements BPEventChannel
{
	protected Map<String, Queue<WeakReference<Consumer<? extends BPEvent>>>> m_lmap = new ConcurrentHashMap<String, Queue<WeakReference<Consumer<? extends BPEvent>>>>();
	protected Lock m_lock = new ReentrantLock();

	public void on(String key, Consumer<? extends BPEvent> listener)
	{
		Queue<WeakReference<Consumer<? extends BPEvent>>> ls = getListeners(key, true);
		boolean isreged = false;
		for (WeakReference<Consumer<? extends BPEvent>> l : ls)
		{
			if (l.get() == listener)
			{
				isreged = true;
				break;
			}
		}
		if (!isreged)
			ls.add(new WeakReference<Consumer<? extends BPEvent>>(listener));
	}

	public void off(String key, Consumer<? extends BPEvent> listener)
	{
		Queue<WeakReference<Consumer<? extends BPEvent>>> ls = getListeners(key, false);
		if (ls == null)
			return;
		WeakReference<Consumer<? extends BPEvent>> r = null;
		for (WeakReference<Consumer<? extends BPEvent>> l : ls)
		{
			if (l.get() == listener)
			{
				r = l;
				break;
			}
		}
		if (r != null)
			ls.remove(r);
	}

	protected Queue<WeakReference<Consumer<? extends BPEvent>>> getListeners(String key, boolean iswrite)
	{
		Queue<WeakReference<Consumer<? extends BPEvent>>> ls = null;
		ls = m_lmap.get(key);
		if (ls == null && iswrite)
		{
			m_lock.lock();
			try
			{
				ls = m_lmap.get(key);
				if (ls == null)
				{
					ls = new ConcurrentLinkedQueue<WeakReference<Consumer<? extends BPEvent>>>();
					m_lmap.put(key, ls);
				}
			}
			finally
			{
				m_lock.unlock();
			}
		}
		else
		{

		}
		return ls;
	}

	public boolean trigger(BPEvent event)
	{
		Queue<WeakReference<Consumer<? extends BPEvent>>> ls = getListeners(event.key, false);
		boolean rc = false;
		if (ls != null)
		{
			List<WeakReference<Consumer<? extends BPEvent>>> bads = new ArrayList<WeakReference<Consumer<? extends BPEvent>>>();
			List<Consumer<? extends BPEvent>> tls = new ArrayList<Consumer<? extends BPEvent>>();
			for (WeakReference<Consumer<? extends BPEvent>> ref : ls)
			{
				Consumer<? extends BPEvent> l = ref.get();
				if (l != null)
				{
					tls.add(l);
				}
				else
					bads.add(ref);
			}
			ls.removeAll(bads);
			rc = triggerEventListeners(tls, event);
		}
		return rc;
	}

	protected boolean triggerEventListeners(List<Consumer<? extends BPEvent>> ls, BPEvent event)
	{
		return triggerEventListenersInner(ls, event);
	}

	protected boolean triggerEventListenersInner(List<Consumer<? extends BPEvent>> ls, BPEvent event)
	{
		for (Consumer<? extends BPEvent> l : ls)
		{
			triggerEventListener(l, event);
			if (event.stopNext)
			{
				break;
			}
		}
		return event.stopDefault;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void triggerEventListener(Consumer<? extends BPEvent> listener, BPEvent event)
	{
		((Consumer) listener).accept(event);
	}
}
