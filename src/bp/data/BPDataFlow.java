package bp.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import bp.transform.BPTransformer;
import bp.util.LogicUtil;
import bp.util.ObjUtil;
import bp.util.Std;

public class BPDataFlow extends BPDataConsumer.BPDataConsumerBase<Object> implements BPSLData
{
	protected List<BPDataConsumer<?>> m_children = new ArrayList<BPDataConsumer<?>>();
	protected String m_rootid;
	protected boolean m_istf;

	protected Map<String, String> m_links = new HashMap<String, String>();

	public String getInfo()
	{
		return "Data Flow";
	}

	public List<BPDataConsumer<?>> getChildren()
	{
		return new ArrayList<BPDataConsumer<?>>(m_children);
	}

	public void accept(Object t)
	{
		Map<String, BPDataConsumer<?>> chmap = new HashMap<String, BPDataConsumer<?>>();
		String nid = m_rootid;
		String nanchor = null;
		String nkey = nid + (nanchor == null ? "" : "_" + nanchor);
		Object ndata = t;
		Map<String, Object> context = initContext();
		BPDataConsumerDataHolder<Object> dc = new BPDataConsumerDataHolder<Object>();
		boolean istf;
		while (nid != null)
		{
			BPDataConsumer<?> n = chmap.get(nkey);
			if (n != null)
			{
				istf = n.isTransformer();
				n.setFromAnchor(nanchor);
				n.setContext(context);
				dc.clear();
				if (istf)
					((BPTransformer<?>) n).setOutput(dc);
				try
				{
					n.runSegmentWithData(ndata);
				}
				finally
				{
					n.setFromAnchor(null);
					if (istf)
						((BPTransformer<?>) n).setOutput(null);
					n.setContext(null);
				}
				ndata = dc.getData();
				nanchor = dc.getOutAnchor();
				nkey = nid + (nanchor == null ? "" : "_" + nanchor);
			}
			else
			{
				Std.err(nid + " not found");
				break;
			}
		}
	}

	protected Map<String, Object> initContext()
	{
		if (m_contextref != null)
			return m_contextref.get();
		else
		{
			Map<String, Object> context = new ConcurrentHashMap<String, Object>();
			m_contextref = new BPDataWrapper<Map<String, Object>>(context);
			return context;
		}
	}

	public void setLinks(Map<String, ?> links)
	{
		m_links.clear();
		for (Entry<String, ?> entry : links.entrySet())
			m_links.put(entry.getKey(), ObjUtil.toString(entry.getValue()));
	}

	public void setLink(BPDataConsumer<?> c0, BPDataConsumer<?> c1, String anchor0, String anchor1)
	{
		m_links.put(c0.getID() + (anchor0 == null ? "" : "_" + anchor0), c1.getID() + (anchor1 == null ? "" : "_" + anchor1));
	}

	public void setIsTransformer(boolean flag)
	{
		m_istf = flag;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		List<BPDataConsumer<?>> chs = m_children;
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
