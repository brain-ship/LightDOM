package net.ropelato.lightdom;

import java.io.IOException;
import java.io.Writer;

public class TextNode implements Node
{
	private Element parent;
	private String text;

	public TextNode()
	{
		this.parent = null;
		this.text = null;
	}

	public TextNode(String text)
	{
		this();
		this.text = text;
	}

	public void setParent(Element element)
	{
		this.parent = element;
	}

	public Element getParent()
	{
		return parent;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void appendText(String text)
	{
		if(text != null)
		{
			if(this.text == null)
				this.text = "";
			this.text = this.text + text;
		}
	}

	public String getText()
	{
		return text;
	}

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
}
