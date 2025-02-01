package bp.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import bp.id.UUIDGenerator;
import bp.util.DiagramUtil;
import bp.util.ObjUtil;

public class BPDiagram implements BPSLData
{
	protected Map<String, BPDiagramElement> m_elemap = new HashMap<String, BPDiagramElement>();
	protected List<BPDiagramLayer> m_layers = new ArrayList<BPDiagramLayer>();
	protected String m_name;

	public void registerElement(BPDiagramElement element)
	{
		m_elemap.put(element.key, element);
	}

	public String getName()
	{
		return m_name;
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public void unregisterElement(BPDiagramElement element)
	{
		m_elemap.remove(element.key);
	}

	public List<BPDiagramLayer> getLayers()
	{
		return new ArrayList<BPDiagramLayer>(m_layers);
	}

	public BPDiagramLayer getLayer(String name)
	{
		List<BPDiagramLayer> layers = m_layers;
		for (BPDiagramLayer layer : layers)
		{
			if (name.equals(layer.getName()))
				return layer;
		}
		return null;
	}

	public BPDiagramLayer getLayerByID(int id)
	{
		List<BPDiagramLayer> layers = m_layers;
		for (BPDiagramLayer layer : layers)
		{
			if (id == layer.getID())
				return layer;
		}
		return null;
	}

	public Map<Integer, BPDiagramLayer> getLayerMap()
	{
		Map<Integer, BPDiagramLayer> rc = new HashMap<Integer, BPDiagramLayer>();
		for (BPDiagramLayer layer : m_layers)
		{
			rc.put(layer.getID(), layer);
		}
		return rc;
	}

	public BPDiagramElement findElement(String key)
	{
		return m_elemap.get(key);
	}

	public void eachElement(BiConsumer<BPDiagramElement, BPDiagramLayer> cb)
	{
		for (BPDiagramLayer layer : m_layers)
		{
			List<BPDiagramElement> eles = layer.getElements();
			for (BPDiagramElement ele : eles)
			{
				cb.accept(ele, layer);
			}
		}
	}

	public double[] calcBounds()
	{
		double xmin = Double.MAX_VALUE;
		double xmax = 0 - Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double ymax = 0 - Double.MAX_VALUE;
		for (BPDiagramLayer layer : m_layers)
		{
			for (BPDiagramElement ele : layer.getElements())
			{
				if (ele.getElementType() == BPDiagramElement.ELEMENTTYPE_NODE)
				{
					BPDiagramNode node = (BPDiagramNode) ele;
					double x = node.x;
					double y = node.y;
					if (x < xmin)
						xmin = x;
					if (x > xmax)
						xmax = x;
					if (y < ymin)
						ymin = y;
					if (y > ymax)
						ymax = y;
				}
			}
		}
		return new double[] { xmin, ymin, xmax, ymax };
	}

	public void addLayer(BPDiagramLayer layer)
	{
		m_layers.add(layer);
		layer.setDiagram(this);
		layer.setID(m_layers.size());
	}

	public void removeLayer(BPDiagramLayer layer)
	{
		m_layers.remove(layer);
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		List<Map<String, Object>> layers = new ArrayList<Map<String, Object>>();
		for (BPDiagramLayer layer : m_layers)
		{
			layers.add(layer.getSaveData());
		}
		rc.put("name", m_name);
		rc.put("layers", layers);
		return rc;
	}

	@SuppressWarnings("unchecked")
	public void setMappedData(Map<String, Object> data)
	{
		m_name = ObjUtil.toString(data.get("name"));
		List<Map<String, Object>> layers = (List<Map<String, Object>>) data.get("layers");
		m_layers.clear();
		for (Map<String, Object> layer : layers)
		{
			BPDiagramLayer dlayer = ObjUtil.mapToObj2(layer, false);
			addLayer(dlayer);
			List<BPDiagramElement> eles = dlayer.getElements();
			for (BPDiagramElement ele : eles)
			{
				registerElement(ele);
				ele.layerid = dlayer.getID();
			}
		}
		for (BPDiagramLayer layer : m_layers)
		{
			List<BPDiagramElement> eles = layer.getElements();
			for (BPDiagramElement ele : eles)
			{
				if (ele.getElementType() == 2)
					((BPDiagramLink) ele).linkNode(m_elemap);
			}
		}
	}

	public static class BPDiagramElementState
	{
		public boolean selected;
	}

	public abstract static class BPDiagramElement implements BPSLData
	{
		public String label;
		public String key;
		public Object userdata;
		public byte colorindex;
		public BPDiagramElementState state;
		public int layerid;
		public double[] measuresize;
		public int measureflag;

		public final static int ELEMENTTYPE_NODE = 1;
		public final static int ELEMENTTYPE_LINK = 2;

		protected BPDiagramElement()
		{
		}

		public BPDiagramElement(String key)
		{
			this.key = key;
		}

		public abstract int getElementType();

		public boolean isSelected()
		{
			return state == null ? false : state.selected;
		}

		public void setSelected(boolean selected)
		{
			if (state == null)
				state = new BPDiagramElementState();
			state.selected = selected;
		}

		public Map<String, Object> getMappedData()
		{
			Map<String, Object> rc = new HashMap<String, Object>();
			rc.put("label", label);
			rc.put("key", key);
			rc.put("userdata", ObjUtil.objToMap(userdata));
			return rc;
		}

		@SuppressWarnings("unchecked")
		public void setMappedData(Map<String, Object> data)
		{
			label = ObjUtil.toString(data.get("label"));
			key = ObjUtil.toString(data.get("key"));
			userdata = ObjUtil.mapToObj2((Map<String, ?>) data.get("userdata"), true);
		}

		public abstract boolean test(double x, double y);

		public abstract boolean intersectRectangle(double[] rect);

		public void setRandomKey()
		{
			UUIDGenerator gen = new UUIDGenerator();
			key = gen.genID();
		}
	}

	public static class BPDiagramNode extends BPDiagramElement
	{
		public double x;
		public double y;

		public BPDiagramNode()
		{
			super();
		}

		public BPDiagramNode(String key)
		{
			super(key);
		}

		public int getElementType()
		{
			return ELEMENTTYPE_NODE;
		}

		public boolean test(double x, double y)
		{
			boolean rc = false;
			if (measuresize != null)
			{
				double w = measuresize[0] + 4;
				double h = measuresize[1] + 4;
				double x1 = this.x - (w / 2d);
				double x2 = this.x + (w / 2d);
				double y1 = this.y - (h / 2d);
				double y2 = this.y + (h / 2d);
				rc = x1 <= x && x2 >= x && y1 <= y && y2 >= y;
			}
			return rc;
		}

		public Map<String, Object> getMappedData()
		{
			Map<String, Object> rc = super.getMappedData();
			rc.put("x", Double.toString(x));
			rc.put("y", Double.toString(y));
			return rc;
		}

		public void setMappedData(Map<String, Object> data)
		{
			super.setMappedData(data);
			x = ObjUtil.toDouble(data.get("x"), 0d);
			y = ObjUtil.toDouble(data.get("y"), 0d);
		}

		public boolean intersectRectangle(double[] rect)
		{
			if (measuresize != null)
			{
				double w = measuresize[0] + 4;
				double h = measuresize[1] + 4;
				double x1 = this.x - (w / 2d);
				double y1 = this.y - (h / 2d);

				return DiagramUtil.intersects(new double[] { x1, y1, w, h }, rect);
			}
			return false;
		}
	}

	public static class BPDiagramLink extends BPDiagramElement
	{
		public BPDiagramNode n1;
		public BPDiagramNode n2;
		public int linktype;
		private String n1key;
		private String n2key;
		public double[] measurearrowpoint;

		public BPDiagramLink()
		{
			super();
		}

		public BPDiagramLink(String key)
		{
			super(key);
		}

		public int getElementType()
		{
			return ELEMENTTYPE_LINK;
		}

		public boolean test(double x, double y)
		{
			if (n1 == null || n2 == null)
				return false;
			double n1x = n1.x;
			double n1y = n1.y;
			double n2x = n2.x;
			double n2y = n2.y;
			if (n1x == n2x && n1y == n2y)
				return false;
			if (n1x == n2x)
			{
				return y >= n1y && y <= n2y || y <= n1y && y >= n2y;
			}
			else
			{
				double k = (n2y - n1y) / (n2x - n1x);
				double b = n1y - (k * n1x);
				double d = Math.abs(k * x - y + b);
				return d < (2 + (k * k));
			}
		}

		public Map<String, Object> getMappedData()
		{
			Map<String, Object> rc = super.getMappedData();
			rc.put("n1key", n1 == null ? null : n1.key);
			rc.put("n2key", n2 == null ? null : n2.key);
			rc.put("linktype", linktype);
			return rc;
		}

		public void setMappedData(Map<String, Object> data)
		{
			super.setMappedData(data);
			n1key = ObjUtil.toString(data.get("n1key"));
			n2key = ObjUtil.toString(data.get("n2key"));
			linktype = ObjUtil.toInt(data.get("linktype"), 0);
		}

		public void linkNode(Map<String, BPDiagramElement> elemap)
		{
			if (n1key != null)
				n1 = (BPDiagramNode) elemap.get(n1key);
			if (n2key != null)
				n2 = (BPDiagramNode) elemap.get(n2key);
		}

		public boolean intersectRectangle(double[] rect)
		{
			return false;
		}
	}

	public static class BPDiagramLayer implements BPSLData
	{
		protected List<BPDiagramElement> m_elements;
		protected WeakReference<BPDiagram> m_diagramref;
		protected String m_name;
		protected int m_id;

		public BPDiagramLayer()
		{
			m_elements = new ArrayList<BPDiagramElement>();
		}

		public BPDiagramLayer(String name)
		{
			this();
			setName(name);
		}

		public void setID(int id)
		{
			m_id = id;
		}

		public int getID()
		{
			return m_id;
		}

		public void setName(String name)
		{
			m_name = name;
		}

		public String getName()
		{
			return m_name;
		}

		public void setDiagram(BPDiagram diagram)
		{
			m_diagramref = new WeakReference<BPDiagram>(diagram);
		}

		public BPDiagram getDiagram()
		{
			return m_diagramref == null ? null : m_diagramref.get();
		}

		public void addElement(BPDiagramElement element)
		{
			m_elements.add(element);
			getDiagram().registerElement(element);
		}

		public void removeElement(BPDiagramElement element)
		{
			getDiagram().unregisterElement(element);
			m_elements.remove(element);
		}

		public List<BPDiagramElement> getElements()
		{
			return new ArrayList<BPDiagramElement>(m_elements);
		}

		public Map<String, Object> getMappedData()
		{
			Map<String, Object> rc = new HashMap<String, Object>();
			List<Map<String, Object>> eles = new ArrayList<Map<String, Object>>();
			for (BPDiagramElement ele : m_elements)
			{
				eles.add(ele.getSaveData());
			}
			rc.put("name", m_name);
			rc.put("elements", eles);
			return rc;
		}

		@SuppressWarnings("unchecked")
		public void setMappedData(Map<String, Object> data)
		{
			m_name = ObjUtil.toString(data.get("name"));
			List<Map<String, Object>> eles = (List<Map<String, Object>>) data.get("elements");
			m_elements.clear();
			for (Map<String, Object> ele : eles)
			{
				BPDiagramElement dele = ObjUtil.mapToObj2(ele, false);
				m_elements.add(dele);
			}
		}
	}
}
