package net.ropelato.lightdom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents an element in the DOM tree. It has a name and optionally an id as well as attributes and children.
 *
 * @author Sandro Ropelato
 * @version 1.1.1
 */
public class Element implements Node
{
	private static final Logger logger = Logger.getLogger(Element.class.getName());

	private final String name;
	private String id;
	private Element parent;
	private final Map<String, String> attributes = new HashMap<String, String>();
	private final List<Node> children = new ArrayList<Node>();
	private final List<TextNode> textNodes = new ArrayList<TextNode>();
	private final Map<String, List<Element>> elementsByName = new HashMap<String, List<Element>>();
	private final Map<String, Element> elementsById = new HashMap<String, Element>();

	/**
	 * Creates a new element.
	 *
	 * @param name name of the element
	 */
	public Element(String name)
	{
		this(name, null, null);
	}

	/**
	 * Creates an element with the name and id.
	 *
	 * @param name name of the element
	 * @param id   id of the element
	 */
	public Element(String name, String id)
	{
		this(name, id, null);
	}

	/**
	 * Creates an element with name, id and a map of attributes.
	 *
	 * @param name       name of the element
	 * @param id         id of the element
	 * @param attributes attributes of the element
	 */
	public Element(String name, String id, Map<String, String> attributes)
	{
		this.name = name;
		this.id = id;
		if(attributes != null && !attributes.isEmpty())
			this.attributes.putAll(attributes);
	}

	/**
	 * Creates element based on an instance of org.w3c.dom.Node. This will throw a RuntimeException if the given node is not an instance of org.w3c.dom.Element.
	 *
	 * @return element based on given org.dom.w3c.Node instance
	 * @since 1.1
	 */
	public static Element fromW3CNode(org.w3c.dom.Node w3cNode)
	{
		if(w3cNode.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
			throw new RuntimeException("Node must be an element.");

		// create element
		Element element = new Element(w3cNode.getNodeName());

		// set attributes
		NamedNodeMap attributesMap = w3cNode.getAttributes();
		for(int i = 0; i < attributesMap.getLength(); i++)
		{
			org.w3c.dom.Node attributeNode = attributesMap.item(i);
			element.setAttribute(attributeNode.getNodeName(), attributeNode.getNodeValue());
		}

		// append child nodes
		NodeList childNodes = w3cNode.getChildNodes();
		for(int i = 0; i < childNodes.getLength(); i++)
		{
			org.w3c.dom.Node childNode = childNodes.item(i);
			if(childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
			{
				element.appendChild(Element.fromW3CNode(childNode));
			}
			else if(childNode.getNodeType() == org.w3c.dom.Node.TEXT_NODE)
			{
				element.appendChild(TextNode.fromW3CNode(childNode));
			}
		}

		return element;
	}

	/**
	 * Converts node to an instance of org.w3c.dom.Node in the context of the given document.
	 *
	 * @param document document in which the new node will be created
	 * @return instance of org.w3c.dom.Node
	 * @since 1.1
	 */
	public org.w3c.dom.Node toW3CNode(org.w3c.dom.Document document)
	{
		org.w3c.dom.Element element = document.createElement(name);

		// append attributes
		for(Map.Entry<String, String> attributeEntry : attributes.entrySet())
		{
			element.setAttribute(attributeEntry.getKey(), attributeEntry.getValue());
			if("id".equalsIgnoreCase(attributeEntry.getKey()))
				element.setIdAttribute(attributeEntry.getKey(), true);
		}

		// append children
		for(Node childNode : getChildren())
		{
			element.appendChild(childNode.toW3CNode(document));
		}

		return element;
	}

	/**
	 * Adds an attribute to the element.
	 *
	 * @param name  name of the attribute
	 * @param value value of the attribute
	 */
	public void setAttribute(String name, String value)
	{
		if("id".equalsIgnoreCase(name))
			setId(value);
		else
			attributes.put(name, value);
	}

	/**
	 * Returns attribute with the corresponding name.
	 *
	 * @param name name of the attribute
	 * @return attribute value or {@code null} if no attribute with the corresponding name exists
	 */
	public String getAttribute(String name)
	{
		if("id".equalsIgnoreCase(name))
			return getId();
		else
			return attributes.get(name);
	}

	/**
	 * Returns attribute with the corresponding name interpreted as byte. This method will throw a NullPointerException if there is no attribute with the corresponding name exists and a NumberFormatException if it cannot be interpreted as byte.
	 *
	 * @param name name of the attribute
	 * @return attribute value as byte
	 */
	public byte getAttributeAsByte(String name)
	{
		return new Double(getAttribute(name)).byteValue();
	}

	/**
	 * Returns attribute with the corresponding name interpreted as short. This method will throw a NullPointerException if there is no attribute with the corresponding name exists and a NumberFormatException if it cannot be interpreted as short.
	 *
	 * @param name name of the attribute
	 * @return attribute value as short
	 */
	public short getAttributeAsShort(String name)
	{
		return new Double(getAttribute(name)).shortValue();
	}

	/**
	 * Returns attribute with the corresponding name interpreted as int. This method will throw a NullPointerException if there is no attribute with the corresponding name exists and a NumberFormatException if it cannot be interpreted as int.
	 *
	 * @param name name of the attribute
	 * @return attribute value as int
	 */
	public int getAttributeAsInt(String name)
	{
		return new Double(getAttribute(name)).intValue();
	}

	/**
	 * Returns attribute with the corresponding name interpreted as long. This method will throw a NullPointerException if there is no attribute with the corresponding name exists and a NumberFormatException if it cannot be interpreted as long.
	 *
	 * @param name name of the attribute
	 * @return attribute value as long
	 */
	public long getAttributeAsLong(String name)
	{
		return new Double(getAttribute(name)).longValue();
	}

	/**
	 * Returns attribute with the corresponding name interpreted as float. This method will throw a NullPointerException if there is no attribute with the corresponding name exists and a NumberFormatException if it cannot be interpreted as float.
	 *
	 * @param name name of the attribute
	 * @return attribute value as float
	 */
	public float getAttributeAsFloat(String name)
	{
		return new Double(getAttribute(name)).floatValue();
	}

	/**
	 * Returns attribute with the corresponding name interpreted as double. This method will throw a NullPointerException if there is no attribute with the corresponding name exists and a NumberFormatException if it cannot be interpreted as double.
	 *
	 * @param name name of the attribute
	 * @return attribute value as double
	 */
	public double getAttributeAsDouble(String name)
	{
		return new Double(getAttribute(name)).doubleValue();
	}

	/**
	 * Returns attribute with the corresponding interpreted as boolean.
	 *
	 * @param name name of the attribute
	 * @return attribute value as boolean or {@code false} if there is no attribute with the corresponding name or the attribute cannot be interpreted as boolean
	 */
	public boolean getAttributeAsBoolean(String name)
	{
		return new Boolean(getAttribute(name)).booleanValue();
	}

	/**
	 * Returns the map of attributes.
	 *
	 * @return map of attributes
	 */
	public Map<String, String> getAttributes()
	{
		return attributes;
	}

	/**
	 * Return the text in this element. It traverses the list of all children in the order in which they have been added and recursively invokes the getText method on each of them.
	 *
	 * @return text in this element
	 */
	public String getText()
	{
		if(textNodes.isEmpty())
			return null;

		String text = "";
		for(Node node : children)
		{
			if(node instanceof TextNode)
			{
				text += ((TextNode)node).getText();
			}
			else if(node instanceof Element)
			{
				String tmpText = ((Element)node).getText();
				if(tmpText != null)
					text += tmpText;
			}
		}
		return text;
	}

	/**
	 * Returns the text in this element interpreted as byte. This method will throw a NullPointerException if there is no text in this element and a NumberFormatException if it cannot be interpreted as byte.
	 *
	 * @return text in this element as byte
	 */
	public byte getTextAsByte()
	{
		return new Double(getText()).byteValue();
	}

	/**
	 * Returns the text in this element interpreted as short. This method will throw a NullPointerException if there is no text in this element and a NumberFormatException if it cannot be interpreted as short.
	 *
	 * @return text in this element as short
	 */
	public short getTextAsShort()
	{
		return new Double(getText()).shortValue();
	}

	/**
	 * Returns the text in this element interpreted as int. This method will throw a NullPointerException if there is no text in this element and a NumberFormatException if it cannot be interpreted as int.
	 *
	 * @return text in this element as int
	 */
	public int getTextAsInt()
	{
		return new Double(getText()).intValue();
	}

	/**
	 * Returns the text in this element interpreted as long. This method will throw a NullPointerException if there is no text in this element and a NumberFormatException if it cannot be interpreted as long.
	 *
	 * @return text in this element as long
	 */
	public long getTextAsLong()
	{
		return new Double(getText()).longValue();
	}

	/**
	 * Returns the text in this element interpreted as float. This method will throw a NullPointerException if there is no text in this element and a NumberFormatException if it cannot be interpreted as float.
	 *
	 * @return text in this element as float
	 */
	public float getTextAsFloat()
	{
		return new Double(getText()).floatValue();
	}

	/**
	 * Returns the text in this element interpreted as double. This method will throw a NullPointerException if there is no text in this element and a NumberFormatException if it cannot be interpreted as double.
	 *
	 * @return text in this element as double
	 */
	public double getTextAsDouble()
	{
		return new Double(getText()).doubleValue();
	}

	/**
	 * Returns the text in this element interpreted as boolean.
	 *
	 * @return text as boolean or {@code false} if there is no text in this element or the text cannot be interpreted as boolean
	 */
	public boolean getTextAsBoolean()
	{
		return new Boolean(getText()).booleanValue();
	}

	/**
	 * Returns list of all text nodes.
	 *
	 * @return list of all text nodes in the order in which they have been added
	 */
	public List<TextNode> getTextNodes()
	{
		return textNodes;
	}

	/**
	 * Returns first element with the corresponding name. If the name contains slashes this method will interpret this as a path to the element.
	 *
	 * @param name name of the element
	 * @return first element matching the name or {@code null} if no element matches
	 */
	public Element getElementByName(String name)
	{
		List<Element> result = getElementsByName(name);
		if(result.size() == 0)
			return null;
		else
			return result.get(0);
	}

	/**
	 * Indicates whether this element has any children.
	 *
	 * @return {@code true} if this element has children, {@code false} otherwise
	 */
	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	/**
	 * Indicates whether this element has at least one child element with the corresponding name. If the name contains slashes this method will interpret this as a path to the element.
	 *
	 * @param name name of the element
	 * @return {@code true} if this element has child elements with the corresponding name, {@code false} otherwise
	 */
	public boolean hasElementWithName(String name)
	{
		if(elementsByName.containsKey(name))
			return true;
		else
			return getElementByName(name) != null;
	}

	/**
	 * Indicates whether this element has a child element with the corresponding id.
	 *
	 * @param id id of the element
	 * @return {@code true} if this element has a child with the corresponding id, {@code false} otherwise
	 */
	public boolean hasElementWithId(String id)
	{
		return elementsById.containsKey(id);
	}

	/**
	 * Indicates whether this element has at least one attribute with the corresponding name.
	 *
	 * @param name name of the attribute
	 * @return {@code true} if this element has attributes with the corresponding name, {@code false} otherwise
	 */
	public boolean hasAttributeWithName(String name)
	{
		return attributes.containsKey(name);
	}

	/**
	 * Returns all elements with the corresponding name. If the name contains slashes this method will interpret this as a path to the element.
	 *
	 * @param name name of the elements
	 * @return list of elements matching the name or an empty list if none match
	 */
	public List<Element> getElementsByName(String name)
	{
		if(name == null)
			return new ArrayList<Element>();

		while(name.startsWith("/"))
			name = name.substring(1);

		while(name.endsWith("/"))
			name = name.substring(0, name.length() - 1);

		if(name.length() == 0)
		{
			return new ArrayList<Element>();
		}
		else
		{
			if(name.contains("/"))
			{
				Element parentElement = null;
				String[] tagNames = name.split("/");
				for(int i = 0; i < tagNames.length; i++)
				{
					String tagName = tagNames[i];
					if(i == tagNames.length - 1)
					{
						return parentElement.getElementsByName(tagName);
					}
					else
					{
						if(parentElement == null)
							parentElement = getElementByName(tagName);
						else
							parentElement = parentElement.getElementByName(tagName);

						if(parentElement == null)
							return new ArrayList<Element>();
					}
				}
				return null;
			}
			else
			{
				List<Element> result = elementsByName.get(name);
				if(result == null)
					return new ArrayList<Element>();
				else
					return result;
			}
		}
	}

	/**
	 * Returns the element with the corresponding id.
	 *
	 * @param id id of the element
	 * @return element with the corresponding id or {@code null} if no such element exists
	 */
	public Element getElementById(String id)
	{
		return elementsById.get(id);
	}

	/**
	 * Returns a list with all child elements in the order in which they have been added.
	 *
	 * @return list of all child elements
	 */
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

	/**
	 * Returns a list of all children in the order in which they have been added.
	 *
	 * @return list of all children
	 */
	public List<Node> getChildren()
	{
		return children;
	}

	/**
	 * Sets the parent of this element.
	 *
	 * @param parent parent of this element
	 */
	public void setParent(Element parent)
	{
		this.parent = parent;
	}

	/**
	 * Returns the parent of this element.
	 *
	 * @return parent of this element
	 */
	public Element getParent()
	{
		return parent;
	}

	/**
	 * Returns the name of this element.
	 *
	 * @return name of this element
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the id of this element.
	 *
	 * @param id id of this element
	 */
	public void setId(String id)
	{
		this.id = id;
		if(parent != null)
		{
			parent.appendChild(this);
		}
	}

	/**
	 * Returns the id of this element.
	 *
	 * @return id of this element
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Appends a child node to this element.
	 *
	 * @param node new child node
	 */
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

		if(node instanceof TextNode)
		{
			textNodes.add((TextNode)node);
		}
	}

	/**
	 * Removes child node from this element.
	 *
	 * @param node child node to be removed
	 */
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

		if(node instanceof TextNode)
		{
			textNodes.remove(node);
		}
	}

	/**
	 * Writes this element and all its children in XML notation.
	 *
	 * @param writer  writer to which the element should be written
	 * @param indent  number of indents (tabs)
	 * @param newLine {@code true} if this element starts on a new line
	 * @throws IOException if an I/O error occurs
	 */
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

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null) return false;
		if(!(o instanceof Element)) return false;

		Element element = (Element)o;

		// compare name
		if(!name.equals(element.getName())) return false;

		// compare id
		if((id == null && element.getId() != null) || (id != null && !id.equals(element.getId()))) return false;

		// compare attributes
		if(attributes.size() != element.getAttributes().size()) return false;
		for(Map.Entry<String, String> attributeEntry : attributes.entrySet())
		{
			if(!attributeEntry.getValue().equals(element.getAttribute(attributeEntry.getKey()))) return false;
		}

		// compare children
		if(children.size() != element.getChildren().size()) return false;
		for(Node child : children)
		{
			boolean foundEqualChild = true;
			for(Node otherChild : element.getChildren())
			{
				if(child.equals(otherChild)) foundEqualChild = true;
			}
			if(!foundEqualChild) return false;
		}

		return true;
	}
}
