package net.ropelato.lightdom;

import java.io.IOException;
import java.io.Writer;

public interface Node
{
	public void setParent(Element element);

	public Element getParent();

	public void write(Writer writer, int indent, boolean newLine) throws IOException;
}
