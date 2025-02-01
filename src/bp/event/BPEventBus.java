package bp.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BPEventBus
{
	protected List<BPEventChannel> m_chs = new ArrayList<BPEventChannel>();

	protected Object m_lock = new Object();
	
	public BPEventBus()
	{
		m_chs.add(null);
	}

	public int addChannel(BPEventChannel ch)
	{
		int id = -1;
		synchronized (m_lock)
		{
			id = m_chs.size();
			m_chs.add(id, ch);
		}
		return id;
	}

	public void removeChannel(int id)
	{
		synchronized (m_lock)
		{
			m_chs.set(id, null);
		}
	}

	public void removeAllChannel()
	{
		synchronized (m_lock)
		{
			m_chs.clear();
		}
	}

	public void on(int channelid, String key, Consumer<? extends BPEvent> listener)
	{
		BPEventChannel ch = m_chs.get(channelid);
		if (ch != null)
			ch.on(key, listener);
	}

	public void off(int channelid, String key, Consumer<? extends BPEvent> listener)
	{
		BPEventChannel ch = m_chs.get(channelid);
		if (ch != null)
			ch.off(key, listener);
	}

	public boolean trigger(int channelid, BPEvent event)
	{
		boolean rc = false;
		BPEventChannel ch = m_chs.get(channelid);
		if (ch != null)
			rc = ch.trigger(event);
		return rc;
	}
}
