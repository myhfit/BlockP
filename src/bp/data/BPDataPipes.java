package bp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.transform.BPTransformer;
import bp.util.ObjUtil;
import bp.util.Std;

public abstract class BPDataPipes extends BPDataConsumer.BPDataConsumerBase<Object> implements BPSLData
{
	protected List<BPDataConsumer<?>> m_children = new ArrayList<BPDataConsumer<?>>();

	public List<BPDataConsumer<?>> getChildren()
	{
		return new ArrayList<BPDataConsumer<?>>(m_children);
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

	public String getInfo()
	{
		return "Data Pipes";
	}

	public static class BPDataPipesDirect extends BPDataPipes
	{
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
			c0.runSegmentWithData(t);
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
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void run(Object source)
	{
		List<BPDataConsumer<?>> pipes = new ArrayList<BPDataConsumer<?>>(m_children);
		int l = pipes.size();
		if (l > 0)
		{
			BPDataConsumer<?> p0 = pipes.get(0);
			if (l > 0)
			{
				BPDataConsumer<?> cp = p0;
				boolean passable = true;
				boolean hasend = false;
				for (int i = 1; i < l; i++)
				{
					BPDataConsumer<?> p = pipes.get(i);
					if (cp.isTransformer())
					{
						((BPTransformer<?>) cp).setOutput(p);
					}
					else if (i < l - 1)
					{
						passable = false;
						break;
					}
					cp = p;
				}
				if (pipes.get(l - 1).isEndpoint())
					hasend = true;
				if (!passable)
				{
					Std.info("Pipe impassable");
					return;
				}
				if (!hasend)
				{
					Std.info("No endpoint");
					return;
				}
			}
			try
			{
				p0.runSegment(() -> ((BPDataConsumer) p0).accept(source));
			}
			catch (Exception e2)
			{
				throw new RuntimeException(e2);
			}
		}
	}
}
