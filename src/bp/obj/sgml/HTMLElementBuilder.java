package bp.obj.sgml;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bp.obj.sgml.HTMLElement.HTMLElementTag;
import bp.obj.sgml.HTMLElement.TextElement;
import bp.util.ObjUtil;

public class HTMLElementBuilder
{
	protected LinkedList<HTMLElement> m_eles;

	public HTMLElementBuilder()
	{
		m_eles = new LinkedList<HTMLElement>();
	}

	public HTMLElementBuilder addVirtualNodeAndEnter()
	{
		return addChildAndEnter(new HTMLElement(HTMLElementTag.NULL));
	}

	public HTMLElementBuilder addDocType()
	{
		addChild(new HTMLElement(HTMLElementTag.DOCTYPE));
		return this;
	}

	public final static HTMLElement createSTHTML()
	{
		return new HTMLElement(HTMLElementTag.html, ObjUtil.makeMap("style", "width:100%;height:100%;margin:0;padding:0;border:0"));
	}

	public HTMLElementBuilder addSTHead()
	{
		addChild(createSTHead());
		return this;
	}

	public final static HTMLElement createSTHead()
	{
		HTMLElement rc = new HTMLElement(HTMLElementTag.head);
		rc.addChild(new HTMLElement(HTMLElementTag.meta, ObjUtil.makeMap("http-equiv", "content-type", "content", "text/html; charset=utf-8")));
		rc.addChild(new HTMLElement(HTMLElementTag.meta, ObjUtil.makeMap("name", "viewport", "content", "initial-scale=1,target-densitydpi=device-dpi")));
		return rc;
	}

	public HTMLElementBuilder addSTBodyAndEnter()
	{
		addChildAndEnter(createSTBody());
		return this;
	}

	public final static HTMLElement createSTBody(Object... params)
	{
		Object[] ps = params;
		if (ps == null || ps.length == 0)
			ps = new Object[] { "style", "width:100%;height:100%;margin:0;border:0" };
		HTMLElement rc = new HTMLElement(HTMLElementTag.body, ObjUtil.makeMap(ps));
		return rc;
	}

	public TextElement createInlineStyle(String... styles)
	{
		TextElement te = new TextElement(HTMLElementTag.style);
		te.setText(String.join("\n", styles));
		return te;
	}

	public TextElement createInlineScript(String... scripts)
	{
		TextElement te = new TextElement(HTMLElementTag.script);
		te.setText(String.join("\n", scripts));
		return te;
	}

	public HTMLElementBuilder addChild(HTMLElementTag tag, Object... params)
	{
		HTMLElement ele = new HTMLElement(tag, ObjUtil.makeMap(params));
		return addChild(ele);
	}

	public HTMLElementBuilder addChild(HTMLElement ele)
	{
		m_eles.getLast().addChild(ele);
		return this;
	}

	public HTMLElementBuilder addChildAndEnter(HTMLElementTag tag, Object... params)
	{
		HTMLElement ele = new HTMLElement(tag, ObjUtil.makeMap(params));
		return addChildAndEnter(ele);
	}

	public HTMLElement create(HTMLElementTag tag, Object... params)
	{
		return new HTMLElement(tag, ObjUtil.makeMap(params));
	}

	public HTMLElement createText(HTMLElementTag tag, String text, Object... params)
	{
		HTMLElement.TextElement rc = new HTMLElement.TextElement(tag, ObjUtil.makeMap(params));
		rc.setText(text);
		return rc;
	}

	public HTMLElementBuilder addChildAndEnter(HTMLElement ele)
	{
		LinkedList<HTMLElement> eles = m_eles;
		if (eles.size() > 0)
			eles.getLast().addChild(ele);
		eles.add(ele);
		return this;
	}

	public HTMLElementBuilder pop()
	{
		m_eles.removeLast();
		return this;
	}

	public HTMLElement current()
	{
		return m_eles.getLast();
	}

	public HTMLElement root()
	{
		return m_eles.getFirst();
	}

	public List<HTMLElement> getElementPath()
	{
		return new ArrayList<HTMLElement>(m_eles);
	}

	public final static HTMLElementBuilder createRawHTML()
	{
		return create().addVirtualNodeAndEnter().addDocType().addChildAndEnter(createSTHTML());
	}

	public final static HTMLElementBuilder createHTML()
	{
		return create().addVirtualNodeAndEnter().addDocType().addChildAndEnter(createSTHTML()).addSTHead().addSTBodyAndEnter();
	}

	public final static HTMLElementBuilder create()
	{
		return new HTMLElementBuilder();
	}
}
