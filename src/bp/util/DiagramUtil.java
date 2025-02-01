package bp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.data.BPDiagram;
import bp.data.BPDiagram.BPDiagramElement;
import bp.data.BPDiagram.BPDiagramLayer;
import bp.data.BPDiagram.BPDiagramLink;
import bp.data.BPDiagram.BPDiagramNode;

public class DiagramUtil
{
	public final static void layout(BPDiagram diagram, Map<String, Object> params)
	{
		Map<String, BPDiagramElement> elemap = new HashMap<String, BPDiagramElement>();
		Map<String, List<String>> eletars = new HashMap<String, List<String>>();
		List<BPDiagramLayer> layers = diagram.getLayers();
		List<BPDiagramNode> nodes = new ArrayList<BPDiagramNode>();
		List<BPDiagramElement> eles = new ArrayList<BPDiagramElement>();
		for (BPDiagramLayer layer : layers)
		{
			for (BPDiagramElement ele : layer.getElements())
			{
				int eletype = ele.getElementType();
				String key = ele.key;
				elemap.put(key, ele);
				eles.add(ele);
				if (eletype == BPDiagramElement.ELEMENTTYPE_NODE)
				{
					nodes.add((BPDiagramNode) ele);
				}
				else if (eletype == BPDiagramElement.ELEMENTTYPE_LINK)
				{
					BPDiagramLink link = (BPDiagramLink) ele;
					if (link.n1 != null && link.n2 != null)
					{
						String n1key = link.n1.key;
						String n2key = link.n2.key;
						List<String> n1tars = eletars.get(n1key);
						if (n1tars == null)
						{
							n1tars = new ArrayList<String>();
							eletars.put(n1key, n1tars);
							n1tars.add(n2key);
						}
						else
						{
							if (!n1tars.contains(n2key))
								n1tars.add(n2key);
						}
						List<String> n2tars = eletars.get(n2key);
						if (n2tars == null)
						{
							n2tars = new ArrayList<String>();
							eletars.put(n2key, n2tars);
							n2tars.add(n1key);
						}
						else
						{
							if (!n2tars.contains(n1key))
								n2tars.add(n1key);
						}
					}
				}
			}
		}
		int gw = 200, gh = 100;
		int gaptop = 100, gapleft = 100;
		double[] pos = new double[] { gaptop, gapleft };
		while (nodes.size() > 0)
		{
			// select root
			String rootkey = null;
			{
				int maxl = -1;
				for (BPDiagramNode node : nodes)
				{
					List<String> eletar = eletars.get(node.key);
					int s = (eletar == null ? 0 : eletar.size());
					if (s > maxl)
					{
						if (node.key == null)
							return;
						rootkey = node.key;
						maxl = s;
					}
				}
			}
			if (rootkey == null && nodes.size() > 0)
			{
				BPDiagramNode node0 = nodes.get(0);
				if (node0.key == null)
					return;
				rootkey = node0.key;
			}
			if (rootkey != null)
			{
				List<String> cur = new ArrayList<String>();
				List<String> all = new ArrayList<String>();
				List<String> next;
				Map<String, List<String>> ptchds = new HashMap<String, List<String>>();
				cur.add(rootkey);
				all.add(rootkey);
				while (cur.size() > 0)
				{
					next = new ArrayList<String>();
					for (String c : cur)
					{
						List<String> eletar = eletars.get(c);
						if (eletar != null)
						{
							for (String et : eletar)
							{
								if (!all.contains(et))
								{
									List<String> ets = ptchds.get(c);
									if (ets == null)
									{
										ets = new ArrayList<String>();
										ptchds.put(c, ets);
									}
									ets.add(et);
									next.add(et);
									all.add(et);
								}
							}
						}
					}
					cur = next;
				}
				lpts(ptchds, pos, (BPDiagramNode) elemap.get(rootkey), eles, nodes, gw, gh, 100, elemap);
			}
		}
	}

	public final static void lpts(Map<String, List<String>> ptchds, double[] pos, BPDiagramNode pt, List<BPDiagramElement> eles, List<BPDiagramNode> nodes, int xgap, int ygap, int node_height, Map<String, BPDiagramElement> elemap)
	{
		BPDiagramNode chd;
		List<String> chds;
		int ty;

		if (ptchds.get(pt.key) == null)
		{
			pt.x = pos[0];
			pt.y = pos[1] - (node_height / 2);
			pos[1] += ygap;
			eles.remove(pt);
			nodes.remove(pt);
		}
		else
		{
			chds = ptchds.get(pt.key);
			pos[0] += xgap;
			ty = (int) pos[1];
			for (String chdkey : chds)
			{
				chd = (BPDiagramNode) elemap.get(chdkey);
				lpts(ptchds, pos, chd, eles, nodes, xgap, ygap, node_height, elemap);
			}
			pos[0] -= xgap;
			pos[1] -= ygap;
			pt.x = pos[0];
			pt.y = (pos[1] + ty) / 2 - (node_height / 2);
			pos[1] += ygap;
			eles.remove(pt);
			nodes.remove(pt);
		}
	}

	public final static void initSimpleDiagram(BPDiagram d)
	{
		if (d.getLayers().size() == 0)
		{
			BPDiagramLayer nlayer = new BPDiagramLayer("node");
			BPDiagramLayer llayer = new BPDiagramLayer("link");
			d.addLayer(llayer);
			d.addLayer(nlayer);
		}
	}

	public final static boolean intersects(double[] rect1, double[] rect2)
	{
		double tw = rect1[2];
		double th = rect1[3];
		double rw = rect2[2];
		double rh = rect2[3];
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0)
		{
			return false;
		}
		double tx = rect1[0];
		double ty = rect1[1];
		double rx = rect2[0];
		double ry = rect2[1];
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		// overflow || intersect
		return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
	}
}
