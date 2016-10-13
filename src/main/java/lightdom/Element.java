package lightdom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an element in the DOM tree. An element has a name and optionally an id as well as attributes and children.
 *
 * @author Sandro Ropelato
 * @version 1.1.5-SNAPSHOT
 */
public class Element implements Node
{
	private static final String INDEX_NAME = "lightdom-element-index";

	private final String name;
	private String id;
	private Element parent;
	private final Map<String, String> attributes = new HashMap<>();
	private final List<Node> children = new ArrayList<>();
	private final List<TextNode> textNodes = new ArrayList<>();
	private final Map<String, List<Element>> elementsByName = new HashMap<>();
	private final Map<String, Element> elementsById = new HashMap<>();
	private org.w3c.dom.Node w3cNodeWithIndex = null;
	private org.w3c.dom.Node w3cNodeWithoutIndex = null;

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
		setIndex("-1");
	}

	/**
	 * Creates element based on an instance of org.w3c.dom.Node. This will throw a RuntimeException if the given node is not an instance of org.w3c.dom.Element.
	 *
	 * @param w3cNode  org.w3c.dom.Node to be used to create new element
	 * @return element based on given org.dom.w3c.Node instance
	 * @since 1.1.0
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
	 * {@inheritDoc}
	 *
	 * Converts node to an instance of org.w3c.dom.Node in the context of the given document.
	 * @since 1.1.0
	 */
	public org.w3c.dom.Node toW3CNode(org.w3c.dom.Document document)
	{
		return toW3CNode(document, false);
	}

	/**
	 * Converts node to an instance of org.w3c.dom.Node in the context of the given document.
	 *
	 * @param document  document in which the new node will be created
	 * @param keepIndex {@code true} if the index attribute (lightdom specific) should be kept, {@code false} otherwise
	 * @return instance of org.w3c.dom.Node
	 * @since 1.1.2
	 */
	protected org.w3c.dom.Node toW3CNode(org.w3c.dom.Document document, boolean keepIndex)
	{
		if(keepIndex)
		{
			if(w3cNodeWithIndex == null)
				w3cNodeWithIndex = createW3CNode(document, true);
			return w3cNodeWithIndex;
		}
		else
		{
			if(w3cNodeWithoutIndex == null)
				w3cNodeWithoutIndex = createW3CNode(document, false);
			return w3cNodeWithoutIndex;
		}
	}

	/**
	 * Creates an instance of org.w3c.dom.Node in the context of the given document.
	 *
	 * @param document  document in which the new node will be created
	 * @param keepIndex {@code true} if the index attribute (lightdom specific) should be kept, {@code false} otherwise
	 * @return instance of org.w3c.dom.Node
	 * @since 1.1.3
	 */
	private org.w3c.dom.Node createW3CNode(org.w3c.dom.Document document, boolean keepIndex)
	{
		org.w3c.dom.Element element = document.createElement(name);

		// set id (if available)
		if(getId() != null)
		{
			element.setAttribute("id", getId());
			element.setIdAttribute("id", true);
		}

		// append attributes
		for(Map.Entry<String, String> attributeEntry : getAttributes().entrySet())
		{
			if(keepIndex || !INDEX_NAME.equals(attributeEntry.getKey()))
				element.setAttribute(attributeEntry.getKey(), attributeEntry.getValue());
		}

		// append children
		for(Node childNode : getChildren())
		{
			if(childNode instanceof Element)
				element.appendChild(((Element)childNode).toW3CNode(document, keepIndex));
			else
				element.appendChild((childNode).toW3CNode(document));
		}

		return element;
	}

	/**
	 * Removes the generated w3c nodes of this element and its parent elements. This method should be invoken if any structural changes have been made to this element.
	 */
	protected void removeW3CNodes()
	{
		w3cNodeWithIndex = null;
		w3cNodeWithoutIndex = null;
		if(parent != null)
			parent.removeW3CNodes();
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
		removeW3CNodes();
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
		return Double.parseDouble(getAttribute(name));
	}

	/**
	 * Returns attribute with the corresponding interpreted as boolean.
	 *
	 * @param name name of the attribute
	 * @return attribute value as boolean or {@code false} if there is no attribute with the corresponding name or the attribute cannot be interpreted as boolean
	 */
	public boolean getAttributeAsBoolean(String name)
	{
		return Boolean.parseBoolean(getAttribute(name));
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
		return Double.parseDouble(getText());
	}

	/**
	 * Returns the text in this element interpreted as boolean.
	 *
	 * @return text as boolean or {@code false} if there is no text in this element or the text cannot be interpreted as boolean
	 */
	public boolean getTextAsBoolean()
	{
		return Boolean.parseBoolean(getText());
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
		return elementsByName.containsKey(name) || getElementByName(name) != null;
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
			return new ArrayList<>();

		while(name.startsWith("/"))
			name = name.substring(1);

		while(name.endsWith("/"))
			name = name.substring(0, name.length() - 1);

		if(name.length() == 0)
		{
			return new ArrayList<>();
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
						if(parentElement != null)
							return parentElement.getElementsByName(tagName);
					}
					else
					{
						if(parentElement == null)
							parentElement = getElementByName(tagName);
						else
							parentElement = parentElement.getElementByName(tagName);

						if(parentElement == null)
							return new ArrayList<>();
					}
				}
				return null;
			}
			else
			{
				List<Element> result = elementsByName.get(name);
				if(result == null)
					return new ArrayList<>();
				else
					return result;
			}
		}
	}

	/**
	 * Retrieves element by Xpath query. This uses the built in XML library for Xpath processing and can be slow.
	 *
	 * @param query Xpath query to search for elements
	 * @return first element matching the query or {@code null} if none match.
	 * @since 1.1.2
	 */
	public Element getElementByQuery(String query)
	{
		List<Element> elementList = getElementsByQuery(query);
		if(elementList.size() == 0)
			return null;
		else
			return elementList.get(0);
	}

	/**
	 * Retrieves elements by Xpath query. This uses the built in XML library for Xpath processing and can be slow.
	 *
	 * @param query Xpath query to search for elements
	 * @return a list containing all elements matching the query
	 * @since 1.1.2
	 */
	public List<Element> getElementsByQuery(String query)
	{
		try
		{
			List<Element> elementList = new ArrayList<>();

			Object result = processXPath(this.toW3CNode(new Document().toW3CDocument(), true), query, XPathConstants.NODESET);
			if(result != null && result instanceof NodeList)
			{
				NodeList nodeList = (NodeList)result;
				for(int i = 0; i < nodeList.getLength(); i++)
				{
					if(nodeList.item(i) != null && nodeList.item(i) instanceof org.w3c.dom.Element)
					{
						Element element = Element.fromW3CNode(nodeList.item(i));

						// find corresponding element relative to this by traversing the DOM tree by child index
						String index = element.getIndex();
						String[] indices = index.split(",");

						Element tmpElement = this;
						for(int j = 1; j < indices.length; j++)
						{
							int childIndex = new Integer(indices[j]);
							tmpElement = (Element)tmpElement.getChildren().get(childIndex);
						}

						// add element to list
						elementList.add(tmpElement);
					}
				}
			}

			return elementList;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns this element's index.
	 *
	 * @return this element's index
	 * @see #setIndex(String)
	 * @since 1.1.2
	 */
	protected String getIndex()
	{
		return getAttribute(INDEX_NAME);
	}

	/**
	 * Updates index of this element and all children. The index is a string of comma-separated integers, identifying which number each element has in the children list of its parent. A -1 means that this element has no parent. For example, {@code "-1,3,0,2"} identifies the 3rd element of the 1st element of the 4th element of the root element.
	 *
	 * @param index index for this element
	 * @since 1.1.2
	 */
	private void setIndex(String index)
	{
		setAttribute(INDEX_NAME, index);
		int i = 0;
		for(Node child : children)
		{
			if(child instanceof Element)
				((Element)child).setIndex(index + "," + i);
			i++;
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
		List<Element> childElements = new ArrayList<>();
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
	 * {@inheritDoc}
	 *
	 * Sets the parent of this element. For any Element instance {@code element} and Node instance {@code node}, {@code node.setParent(element)} has the same effect as {@code element.appendChild(node)}.
	 */
	public void setParent(Element parent)
	{
		this.parent = parent;
		if(parent == null)
			setIndex("-1");
		else
			parent.appendChild(this, false);
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
		removeW3CNodes();
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
	 * Appends a child node to this element. For any Element instance {@code element} and Node instance {@code node}, {@code node.setParent(element)} has the same effect as {@code element.appendChild(node)}.
	 *
	 * @param node new child node
	 */
	public void appendChild(Node node)
	{
		appendChild(node, false);
	}

	/**
	 * Appends a child node to this element. For any Element instance {@code element} and Node instance {@code node}, {@code node.setParent(element)} has the same effect as {@code element.appendChild(node, true)}.
	 *
	 * @param node            new child node
	 * @param invokeSetParent {@code true} if this method should invoke the {@link lightdom.Node#setParent(Element)} method, {@code false} otherwise
	 * @since 1.1.3
	 */
	protected void appendChild(Node node, boolean invokeSetParent)
	{
		if(node.getParent() != null)
		{
			node.getParent().removeChild(node);
		}

		children.add(node);

		if(invokeSetParent)
			node.setParent(this);

		if(node instanceof Element)
		{
			Element element = (Element)node;

			List<Element> elementList = elementsByName.get(element.getName());
			if(elementList == null)
			{
				elementList = new ArrayList<>();
				elementsByName.put(element.getName(), elementList);
			}
			elementList.add(element);

			if(element.getId() != null)
			{
				elementsById.put(element.getId(), element);
			}

			// update index
			element.setIndex(getIndex() + "," + (children.size() - 1));
		}

		if(node instanceof TextNode)
		{
			textNodes.add((TextNode)node);
		}

		removeW3CNodes();
	}

	/**
	 * Removes child node from this element.
	 *
	 * @param node child node to be removed
	 */
	public void removeChild(Node node)
	{
		// get child index
		int formerChildIndex = -1;
		for(Node childNode : children)
		{
			if(childNode == node)
				break;
			else
				formerChildIndex++;
		}

		// remove child
		node.setParent(null);
		children.remove(node);

		// update index of younger children
		for(int i = formerChildIndex; i < children.size(); i++)
		{
			Node childNode = children.get(i);
			if(childNode instanceof Element)
				((Element)childNode).setIndex(getIndex() + "," + i);
		}

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

		removeW3CNodes();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Writes this element and all its children in XML notation.
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
			writer.write(" id=\"" + Document.encodeValueForWriting(id) + "\"");

		for(Map.Entry<String, String> attribute : attributes.entrySet())
		{
			if(!INDEX_NAME.equals(attribute.getKey()))
				writer.write(" " + Document.encodeValueForWriting(attribute.getKey()) + "=\"" + Document.encodeValueForWriting(attribute.getValue()) + "\"");
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

	/** {@inheritDoc} */
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
		ArrayList<Node> equalChildren = new ArrayList<>(children);
		equalChildren.retainAll(element.getChildren());
		if(equalChildren.size() != children.size())
			return false;

		return true;
	}

	/**
	 * Performs a lookup using an Xpath query.
	 *
	 * @param node       instance of org.w3c.dom.Node on which the query should be executed
	 * @param expression Xpath query to be executed
	 * @param returnType expected return type of the query
	 * @return result of the Xpath query.
	 * @since 1.1.2
	 */
	private static Object processXPath(org.w3c.dom.Node node, String expression, QName returnType)
	{
		try
		{
			return XPathFactory.newInstance().newXPath().compile(expression).evaluate(node, returnType);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
