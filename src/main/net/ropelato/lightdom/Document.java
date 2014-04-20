package net.ropelato.lightdom;

import org.xml.sax.*;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Document extends DefaultHandler
{
	private final Stack<Element> openElements = new Stack<Element>();
	private Element rootElement = null;
	private TextNode openTextNode = null;
	private String encoding = "UTF-8";
	private String version = "1.0";

	public static Document fromInputStream(InputStream inputStream)
	{
		Document doc = new Document();
		doc.parse(inputStream);
		return doc;
	}

	public static Document fromFile(File file)
	{
		Document doc = new Document();
		FileInputStream fileInputStream = null;
		try
		{
			fileInputStream = new FileInputStream(file);
			doc = fromInputStream(fileInputStream);
		}
		catch(FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				if(fileInputStream != null)
					fileInputStream.close();
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		return doc;
	}

	public static Document fromFile(String fileName)
	{
		return fromFile(new File(fileName));
	}

	public Document()
	{

	}

	public void setRootElement(Element rootElement)
	{
		this.rootElement = rootElement;
	}

	public Element getRootElement()
	{
		return rootElement;
	}

	private void parse(InputStream inputStream)
	{
		try
		{
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			InputSource inputSource = new InputSource(inputStream);
			xmlReader.setContentHandler(this);
			xmlReader.parse(inputSource);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void toOutputStream(OutputStream outputStream)
	{
		if(rootElement == null)
			throw new RuntimeException("Document has no root node.");

		OutputStreamWriter writer = null;
		try
		{
			writer = new OutputStreamWriter(outputStream, "UTF-8");
			writer.write("<?xml version=\"" + version + "\" encoding=\"" + encoding + "\"?>");
			rootElement.write(writer, 0, true);
			writer.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				if(writer != null)
					writer.close();
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	public void toFile(File file)
	{
		FileOutputStream fileOutputStream = null;
		try
		{
			fileOutputStream = new FileOutputStream(file);
			toOutputStream(fileOutputStream);
		}
		catch(FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				if(fileOutputStream != null)
					fileOutputStream.close();
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	public void toFile(String fileName)
	{
		toFile(new File(fileName));
	}

	@Override
	public void setDocumentLocator(Locator locator)
	{
		if(locator instanceof Locator2)
		{
			encoding = ((Locator2)locator).getEncoding();
			version = ((Locator2)locator).getXMLVersion();
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXException
	{
		if(openTextNode != null)
			openElements.peek().appendChild(openTextNode);

		openTextNode = null;

		Element parent;
		if(!openElements.empty())
			parent = openElements.peek();
		else
			parent = null;

		String id = null;
		Map<String, String> attributes = new HashMap<String, String>();
		if(atts != null)
		{
			for(int i = 0; i < atts.getLength(); i++)
			{
				if("id".equalsIgnoreCase(atts.getLocalName(i)))
					id = atts.getValue(i);
				else
					attributes.put(atts.getLocalName(i), atts.getValue(i));
			}
		}

		Element element = new Element(localName, id, attributes);
		if(parent != null)
			parent.appendChild(element);

		openElements.push(element);
		if(rootElement == null)
		{
			rootElement = element;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
	{
		if(openTextNode != null)
			openElements.peek().appendChild(openTextNode);

		openTextNode = null;
		openElements.pop();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		String textAsString = new String(ch, start, length);

		textAsString = textAsString.replaceAll("\t", "");
		textAsString = textAsString.replaceAll("^(\r\n)*$", "");
		textAsString = textAsString.replaceAll("^(\n)*$", "");

		if(textAsString.trim().length() > 0)
		{
			if(openTextNode == null)
				openTextNode = new TextNode();
			openTextNode.appendText(textAsString);
		}
	}
}
