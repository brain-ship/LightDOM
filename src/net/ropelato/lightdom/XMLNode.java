package net.ropelato.lightdom;

import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLNode
{
	private final String name;
	private final String id;
	private final XMLNode parent;
	private final Map<String, String> attributes = new HashMap<String, String>();
	private final List<XMLNode> children = new ArrayList<XMLNode>();
	private final Map<String, List<XMLNode>> childrenByName = new HashMap<String, List<XMLNode>>();
	private final Map<String, XMLNode> childrenById = new HashMap<String, XMLNode>();
	private String text = "";

	public XMLNode(String name, XMLNode parent, Attributes atts)
	{
		this.name = name;
		this.parent = parent;
		if(atts != null)
		{
			for(int i = 0; i < atts.getLength(); i++)
			{
				attributes.put(atts.getLocalName(i), atts.getValue(i));
			}
		}
		
		String id = getAttribute("id");
		if(id == null || id.length() == 0)
		{
			id = getAttribute("ID");
		}
		if(id != null && id.length() == 0)
		{
			id = null;
		}

		this.id = id;

		if(parent != null)
		{
			parent.appendChild(this);
		}
	}

	public String getAttribute(String name)
	{
		return attributes.get(name);
	}

	public XMLNode getChildByName(String name)
	{
		List<XMLNode> children = getChildrenByName(name);
		if(children != null && children.size() > 0)
		{
			return children.get(0);
		}
		else
		{
			return null;
		}
	}

	public boolean hasChildWithName(String name)
	{
		return childrenByName.containsKey(name);
	}

	public boolean hasChildWithId(String id)
	{
		return childrenById.containsKey(id);
	}

	public boolean hasAttributeWithName(String name)
	{
		return attributes.containsKey(name);
	}

	public List<XMLNode> getChildrenByName(String name)
	{
		return childrenByName.get(name);
	}

	public XMLNode getChildById(String name)
	{
		return childrenById.get(name);
	}

	public List<XMLNode> getChildren()
	{
		return children;
	}

	public XMLNode getParent()
	{
		return parent;
	}

	public String getName()
	{
		return name;
	}

	public String getId()
	{
		return id;
	}

	public String getText()
	{
		return text.trim();
	}

	public void appendText(String newText)
	{
		text = text + newText;
	}

	public void appendChild(XMLNode childNode)
	{
		children.add(childNode);

		List<XMLNode> nodeList = childrenByName.get(childNode.getName());
		if(nodeList == null)
		{
			nodeList = new ArrayList<XMLNode>();
			childrenByName.put(childNode.getName(), nodeList);
		}
		nodeList.add(childNode);

		if(childNode.getId() != null)
		{
			childrenById.put(childNode.getId(), childNode);
		}
	}

	public String toString()
	{
		return "<" + name + ">";
	}
}
