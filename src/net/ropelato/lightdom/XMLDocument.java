package net.ropelato.lightdom;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Stack;

public class XMLDocument extends DefaultHandler
{
	private final Stack<XMLNode> openNodes = new Stack<XMLNode>();
	private XMLNode rootNode = null;

	public XMLDocument(InputStream inStream)
	{
		parse(inStream);
	}

	public XMLDocument(File inputFile)
	{
		parse(inputFile);
	}

	public XMLDocument(String inputFileName)
	{
		this(new File(inputFileName));
	}

	public XMLNode getRootNode()
	{
		return rootNode;
	}

	private void parse(InputStream inStream)
	{
		try
		{
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			InputSource inputSource = new InputSource(inStream);
			xmlReader.setContentHandler(this);
			xmlReader.parse(inputSource);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void parse(File inputFile)
	{
		try
		{
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			InputSource inputSource = new InputSource(new FileReader(inputFile));
			xmlReader.setContentHandler(this);
			xmlReader.parse(inputSource);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXException
	{
		XMLNode parent;
		if(!openNodes.empty())
		{
			parent = openNodes.peek();
		}
		else
		{
			parent = null;
		}

		XMLNode node = new XMLNode(localName, parent, atts);
		openNodes.push(node);
		if(rootNode == null)
		{
			rootNode = node;
		}
	}

	public void endElement(String uri, String localName, String qName)
	{
		openNodes.pop();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		XMLNode currentNode = openNodes.peek();
		currentNode.appendText(new String(ch, start, length));
	}
}
