package bp.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bp.transform.BPTransformer;
import bp.util.LogicUtil;
import bp.util.ObjUtil;

public abstract class BPDataPipes extends BPDataConsumer.BPDataConsumerBase<Object> implements BPSLData
{
	protected List<BPDataConsumer<?>> m_children = new ArrayList<BPDataConsumer<?>>();
	protected Map<String, String> m_links = new HashMap<String, String>();

	public List<BPDataConsumer<?>> getChildren()
	{
		return new ArrayList<BPDataConsumer<?>>(m_children);
	}

	public Map<String, String> getLinks()
	{
		return new HashMap<String, String>();
	}

	public void setLink(BPDataConsumer<?> c0, BPDataConsumer<?> c1)
	{
		m_links.put(c0.getID(), c1.getID());
	}

	public List<BPDataConsumer<?>> getRawChildren()
	{
		return m_children;
	}

	public void setChildren(List<BPDataConsumer<?>> chs)
	{
		m_children.clear();
		m_children.addAll(chs);
	}

	public void setLinks(Map<String, ?> links)
	{
		m_links.clear();
		for (Entry<String, ?> entry : links.entrySet())
			m_links.put(entry.getKey(), ObjUtil.toString(entry.getValue()));
	}

	public String getInfo()
	{
		return "Data Pipes";
	}

	public static class BPDataPipesDirect extends BPDataPipes
	{
		@SuppressWarnings({ "unchecked" })
		public void accept(Object t)
		{
			List<BPDataConsumer<?>> children = new ArrayList<BPDataConsumer<?>>(m_children);
			int l = children.size();
			if (l == 0)
				return;
			BPDataConsumer<?> c0 = children.get(0);
			BPDataConsumer<?> cur = c0;
			for (int i = 1; i < l; i++)
			{
				BPDataConsumer<?> c = children.get(i);
				if (cur.isTransformer())
					((BPTransformer<?>) cur).setOutput(c);
			}
			c0.runSegment(() -> ((BPDataConsumer<Object>) c0).accept(t));
		}
	}

	public void addChild(BPDataConsumer<?> c)
	{
		m_children.add(c);
	}

	public void removeChild(BPDataConsumer<?> c)
	{
		m_children.remove(c);
	}

	public List<BPDataConsumer<?>> removeChildren(List<BPDataConsumer<?>> chds)
	{
		m_children.removeAll(chds);
		return chds;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		List<BPDataConsumer<?>> chs = getChildren();
		List<Map<String, Object>> chmos = new ArrayList<Map<String, Object>>();
		for (BPDataConsumer<?> chd : chs)
			chmos.add(chd.getSaveData());
		rc.put("children", chmos);
		rc.put("links", new HashMap<String, String>(m_links));
		return rc;
	}

	@SuppressWarnings({ "unchecked" })
	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		List<Map<String, Object>> chmos = (List<Map<String, Object>>) data.get("children");
		m_children.clear();
		if (chmos != null)
		{
			List<BPDataConsumer<?>> chs = new ArrayList<BPDataConsumer<?>>();
			for (Map<String, Object> chmo : chmos)
			{
				BPDataConsumer<?> c = ObjUtil.mapToObj2(chmo, false);
				chs.add(c);
			}
			m_children.addAll(chs);
		}
		m_links.clear();
		LogicUtil.IFVU(data.get("links"), links -> setLinks((Map<String, ?>) links));
	}
}
