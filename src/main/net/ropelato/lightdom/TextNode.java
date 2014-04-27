package net.ropelato.lightdom;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * This class represents an element in the DOM tree. It has a name and optionally an id as well as attributes and children.
 *
 * @author Sandro Ropelato
 * @version 1.1
 */
public class TextNode implements Node
{
	private Element parent;
	private String text;

	/**
	 * Creates a new text node.
	 */
	public TextNode()
	{
		this.parent = null;
		this.text = null;
	}

	/**
	 * Creates a new text node with an initial text.
	 *
	 * @param text initial text
	 */
	public TextNode(String text)
	{
		this();
		this.text = text;
	}

	/**
	 * Creates text node based on an instance of org.w3c.dom.Node. This will throw a RuntimeException if the given node is not an instance of org.w3c.dom.Text.
	 *
	 * @return text node based on given org.dom.w3c.Node instance
	 * @since 1.1
	 */
	public static TextNode fromW3CNode(org.w3c.dom.Node w3cNode)
	{
		if(w3cNode.getNodeType() != org.w3c.dom.Node.TEXT_NODE)
			throw new RuntimeException("Node must be a  text node.");

		return new TextNode(w3cNode.getTextContent());
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
		return document.createTextNode(text);
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
	 * Sets text of this text node.
	 *
	 * @param text new text
	 */
	public void setText(String text)
	{
		this.text = text;
	}

	/**
	 * Appends text to existing text.
	 *
	 * @param text text to be appended
	 */
	public void appendText(String text)
	{
		if(text != null)
		{
			if(this.text == null)
				this.text = "";
			this.text = this.text + text;
		}
	}

	/**
	 * Returns text of this text node.
	 *
	 * @return text of this text node.
	 */
	public String getText()
	{
		return text;
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
		if(text != null && text.length() > 0)
		{
			String indentionString = "";
			for(int i = 0; i < indent; i++)
				indentionString += "\t";

			if(newLine)
			{
				writer.write("\n");
				writer.write(indentionString);
			}

			String printText = text;
			printText = printText.replaceAll("\n", "\n" + indentionString + "\t");
			printText = printText.replaceAll("\n" + indentionString + "\t$", "\n" + indentionString);
			writer.write(printText);
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null) return false;
		if(!(o instanceof TextNode)) return false;

		TextNode textNode = (TextNode)o;

		// compare text
		if((text == null && textNode.getText() != null) || (text != null && !text.equals(textNode.getText())))
			return false;

		return true;
	}
}
