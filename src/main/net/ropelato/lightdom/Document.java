package net.ropelato.lightdom;

import org.xml.sax.*;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * This class is used to generate a new DOM document. It can be created using the default constructor or loaded from a file or an InputStream. An existing document can by saved to a file or to an OutputStream.
 *
 * @author Sandro Ropelato
 * @version 1.0
 * @since 1.0
 */
public class Document extends DefaultHandler
{
	private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
	private static final String DEFAULT_VERSION = "1.0";

	private final Stack<Element> openElements = new Stack<Element>();
	private Element rootElement = null;
	private TextNode openTextNode = null;

	private Charset encoding = DEFAULT_ENCODING;
	private String version = DEFAULT_VERSION;

	/**
	 * Creates a new Document from an InputStream.
	 *
	 * @param inputStream input stream from which the document should be loaded
	 * @return document represented by the data form the input stream
	 */
	public static Document fromInputStream(InputStream inputStream)
	{
		Document doc = new Document();
		doc.parse(inputStream);
		return doc;
	}

	/**
	 * Creates a new document from a file.
	 *
	 * @param file file from which the document should be loaded
	 * @return document represented by the content of the file
	 */
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

	/**
	 * Creates a new document from a file. This method is wrapper for the {@link #fromFile(java.io.File)} method.
	 *
	 * @param fileName absolute or relative path to the file to be loaded
	 * @return document represented by the content of the file
	 */
	public static Document fromFile(String fileName)
	{
		return fromFile(new File(fileName));
	}

	/**
	 * Builds document based on an instance of org.w3c.dom.Document.
	 *
	 * @return document based on given org.dom.w3c.Document instance
	 * @since 1.1
	 */
	public static Document fromW3CDocument(org.w3c.dom.Document w3cDocument)
	{
		Document doc = new Document();

		if(w3cDocument.getXmlEncoding() != null)
			doc.setEncoding(w3cDocument.getXmlEncoding());
		if(w3cDocument.getXmlVersion() != null)
			doc.setVersion(w3cDocument.getXmlVersion());

		if(w3cDocument.getDocumentElement() != null)
			doc.setRootElement(Element.fromW3CNode(w3cDocument.getDocumentElement()));

		return doc;
	}

	/**
	 * Writes XML document to an output stream.
	 *
	 * @param outputStream output stream to which the XML document should be written
	 */
	public void toOutputStream(OutputStream outputStream)
	{
		if(rootElement == null)
			throw new RuntimeException("Document has no root node.");

		OutputStreamWriter writer = null;
		try
		{
			writer = new OutputStreamWriter(outputStream, encoding);
			writer.write("<?xml version=\"" + version + "\" encoding=\"" + encoding.displayName() + "\"?>");
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

	/**
	 * Writes XML document to a file.
	 *
	 * @param file file to which the XML document should be written
	 */
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

	/**
	 * Writes XML document to a file. This method is wrapper for the {@link #toFile(java.io.File)} method.
	 *
	 * @param filename
	 */
	public void toFile(String filename)
	{
		toFile(new File(filename));
	}

	/**
	 * Converts document to an instance of org.w3c.dom.Document.
	 *
	 * @return instance of org.w3c.dom.Document
	 * @since 1.1
	 */
	public org.w3c.dom.Document toW3CDocument()
	{
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			org.w3c.dom.Document w3cDocument = docBuilder.newDocument();

			// set version
			w3cDocument.setXmlVersion(getVersion());

			// append root element
			if(rootElement != null)
				w3cDocument.appendChild(rootElement.toW3CNode(w3cDocument));

			return w3cDocument;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets the root element of this document. Setting a new root element will replace the old one as there can be only one root element.
	 *
	 * @param rootElement root element of the document
	 */
	public void setRootElement(Element rootElement)
	{
		this.rootElement = rootElement;
	}

	/**
	 * Returns the root element of this document.
	 *
	 * @return root element of the document
	 */
	public Element getRootElement()
	{
		return rootElement;
	}

	/**
	 * Parses input stream and builds document.
	 *
	 * @param inputStream input stream from which the document should be loaded
	 */
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

	/**
	 * Returns the encoding of the document.
	 *
	 * @return encoding of the document
	 */
	public Charset getEncoding()
	{
		return encoding;
	}

	/**
	 * Sets the encoding of the document.
	 *
	 * @param charset encoding charset of the document.
	 */
	public void setEncoding(Charset charset)
	{
		this.encoding = charset;
	}

	/**
	 * Sets the encoding of the document. This method is wrapper for the {@link #setEncoding(java.nio.charset.Charset)} method.
	 *
	 * @param charsetName name of the encoding charset of the document.
	 */
	public void setEncoding(String charsetName)
	{
		setEncoding(Charset.forName(charsetName));
	}

	/**
	 * Returns the XML version of the document.
	 *
	 * @return XML version of the document.
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Sets the XML version of the document.
	 *
	 * @param version XML version of the document
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	@Override
	public void setDocumentLocator(Locator locator)
	{
		if(locator instanceof Locator2)
		{
			encoding = Charset.forName(((Locator2)locator).getEncoding());
			version = ((Locator2)locator).getXMLVersion();
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
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
	public void characters(char[] ch, int start, int length) throws SAXException
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

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null) return false;
		if(!(o instanceof Document)) return false;

		Document document = (Document)o;

		// compare encoding
		if((encoding == null && document.getEncoding() != null) || (encoding != null && !encoding.equals(document.getEncoding())))
			return false;

		// compare version
		if((version == null && document.getVersion() != null) || (version != null && !version.equals(document.getVersion())))
			return false;

		if((rootElement == null && document.getRootElement() != null) || (rootElement != null && !rootElement.equals(document.getRootElement())))
			return false;

		return true;
	}
}
