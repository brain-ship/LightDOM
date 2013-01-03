package net.ropelato.lightdom;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Element implements Node
{
	private static final Logger logger = Logger.getLogger(Element.class.getName());

	private String name;
	private String id;
	private Element parent;
	private final Map<String, String> attributes = new HashMap<String, String>();
	private final List<Node> children = new ArrayList<Node>();
	private final Map<String, List<Element>> elementsByName = new HashMap<String, List<Element>>();
	private final Map<String, Element> elementsById = new HashMap<String, Element>();

	public Element(String name)
	{
		this(name, null, null);
	}

	public Element(String name, String id)
	{
		this(name, id, null);
	}

	public Element(String name, String id, Map<String, String> attributes)
	{
		this.name = name;
		this.id = id;
		if(attributes != null && !attributes.isEmpty())
			this.attributes.putAll(attributes);
	}

	public void setAttribute(String name, String value)
	{
		if("id".equalsIgnoreCase(name))
			setId(value);
		else
			attributes.put(name, value);
	}

	public String getAttribute(String name)
	{
		if("id".equalsIgnoreCase(name))
			return getId();
		else
			return attributes.get(name);
	}

	public Element getElementByName(String name)
	{
		List<Element> childrenWithName = getElementsByName(name);
		if(childrenWithName != null && childrenWithName.size() > 0)
		{
			return childrenWithName.get(0);
		}
		else
		{
			return null;
		}
	}

	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	public boolean hasElementWithName(String name)
	{
		return elementsByName.containsKey(name);
	}

	public boolean hasElementWithId(String id)
	{
		return elementsById.containsKey(id);
	}

	public boolean hasAttributeWithName(String name)
	{
		return attributes.containsKey(name);
	}

	public List<Element> getElementsByName(String name)
	{
		return elementsByName.get(name);
	}

	public Element getElementById(String name)
	{
		return elementsById.get(name);
	}

	public List<Element> getElements()
	{
		List<Element> childElements = new ArrayList<Element>();
		for(Node child : children)
		{
			if(child instanceof Element)
				childElements.add((Element)child);
		}
		return childElements;
	}

	public List<Node> getChildren()
	{
		return children;
	}

	public void setParent(Element parent)
	{
		this.parent = parent;
	}

	public Element getParent()
	{
		return parent;
	}

	public String getName()
	{
		return name;
	}

	public void setId(String id)
	{
		this.id = id;
		if(parent != null)
		{
			parent.appendChild(this);
		}
	}

	public String getId()
	{
		return id;
	}

	public void appendChild(Node node)
	{
		if(node.getParent() != null)
		{
			logger.log(Level.WARNING, "Child has been assigned to a different parent node and will be removed from it.");
			node.getParent().removeChild(node);
		}

		node.setParent(this);
		children.add(node);

		if(node instanceof Element)
		{
			Element element = (Element)node;

			List<Element> elementList = elementsByName.get(element.getName());
			if(elementList == null)
			{
				elementList = new ArrayList<Element>();
				elementsByName.put(element.getName(), elementList);
			}
			elementList.add(element);

			if(element.getId() != null)
			{
				if(elementsById.containsKey(element.getId()))
					logger.log(Level.WARNING, "Duplicate element with id '" + element.getId() + "'. Old element will be replaced.");

				elementsById.put(element.getId(), element);
			}
		}
	}

	public void removeChild(Node node)
	{
		node.setParent(null);
		children.remove(node);

		if(node instanceof Element)
		{
			Element element = (Element)node;

			List<Element> childrenWithSameName = getElementsByName(element.getName());
			if(childrenWithSameName != null)
			{
				childrenWithSameName.remove(element);
				if(childrenWithSameName.isEmpty())
					elementsByName.remove(element.getName());
			}

			if(element.getId() != null)
				elementsById.remove(element.getId());
		}
	}

	public void write(Writer writer, int indent, boolean newLine) throws IOException
	{
		if(newLine)
		{
			writer.write("\n");
			for(int i = 0; i < indent; i++)
				writer.write("\t");
		}

		writer.write("<" + name);
		if(id != null)
			writer.write(" id=\"" + id + "\"");

		for(Map.Entry<String, String> attribute : attributes.entrySet())
		{
			writer.write(" " + attribute.getKey() + "=\"" + attribute.getValue() + "\"");
		}

		if(!hasChildren())
		{
			writer.write("/>");
		}
		else
		{
			writer.write(">");
			boolean lastElementIsTextNode = false;
			for(Node childNode : children)
			{
				if(childNode instanceof TextNode)
				{
					childNode.write(writer, indent, false);
					lastElementIsTextNode = true;
				}
				else
				{
					childNode.write(writer, indent + 1, !lastElementIsTextNode);
					lastElementIsTextNode = false;
				}
			}

			if(lastElementIsTextNode)
			{
				writer.write("</" + name + ">");
			}
			else
			{
				writer.write("\n");
				for(int i = 0; i < indent; i++)
					writer.write("\t");
				writer.write("</" + name + ">");
			}
		}
	}
}
