package net.ropelato.lightdom;

import java.io.IOException;
import java.io.Writer;

/**
 * This interfase defines a node in the DOM tree. A node can either be an element or a text node.
 *
 * @author Sandro Ropelato
 * @since 1.0
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
	 * Writes this element and all its children in XML notation.
	 *
	 * @param writer  writer to which the element should be written
	 * @param indent  number of indents (tabs)
	 * @param newLine {@code true} if this element starts on a new line
	 * @throws IOException if an I/O error occurs
	 */
	public void write(Writer writer, int indent, boolean newLine) throws IOException;
}
