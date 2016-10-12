package lightdom;

import java.io.IOException;
import java.io.Writer;

/**
 * Defines a node in the DOM tree. A node can either be an element or a text node.
 *
 * @author Sandro Ropelato
 * @version 1.1.4-SNAPSHOT
 */
public interface Node
{
	/**
	 * Sets the parent of this element.
	 *
	 * @param parent parent of this element
	 */
	void setParent(Element parent);

	/**
	 * Returns the parent of this element. For any Element instance {@code element} and Node instance {@code node}, {@code node.setParent(element)} has the same effect as {@code element.appendChild(node)}.
	 *
	 * @return parent of this element
	 */
	Element getParent();

	/**
	 * Converts node to an instance of org.w3c.dom.Node in the context of the given document.
	 *
	 * @param document document in which the new node will be created
	 * @return instance of org.w3c.dom.Node
	 * @since 1.1.0
	 */
	org.w3c.dom.Node toW3CNode(org.w3c.dom.Document document);

	/**
	 * Writes this element and all its children in XML notation.
	 *
	 * @param writer  writer to which the element should be written
	 * @param indent  number of indents (tabs)
	 * @param newLine {@code true} if this element starts on a new line
	 * @throws java.io.IOException if an I/O error occurs
	 */
	void write(Writer writer, int indent, boolean newLine) throws IOException;
}
