package net.ropelato.lightdom;

import java.io.IOException;
import java.io.Writer;

/**
 * This interfase defines a node in the DOM tree. A node can either be an element or a text node.
 *
 * @author Sandro Ropelato
 * @version 1.1.2
 */
public interface Node
{
	/**
	 * Sets the parent of this element.
	 *
	 * @param parent parent of this element
	 */
	public void setParent(Element parent);

	/**
	 * Returns the parent of this element.
	 *
	 * @return parent of this element
	 */
	public Element getParent();

	/**
	 * Converts node to an instance of org.w3c.dom.Node in the context of the given document.
	 *
	 * @param document document in which the new node will be created
	 * @return instance of org.w3c.dom.Node
	 * @since 1.1.0
	 */
	public org.w3c.dom.Node toW3CNode(org.w3c.dom.Document document);

	/**
	 * Writes this element and all its children in XML notation.
	 *
	 * @param writer  writer to which the element should be written
	 * @param indent  number of indents (tabs)
	 * @param newLine {@code true} if this element starts on a new line
	 * @throws IOException if an I/O error occurs
	 */
	public void write(Writer writer, int indent, boolean newLine) throws IOException;
}
