package lightdom;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class DocumentTest
{
	@Test
	public void testFromFile() throws Exception
	{
		// load document (UTF-8)
		Document doc = Document.fromFile("TestFiles/books.xml");

		// change encoding to ISO-8859-1
		doc.setEncoding(StandardCharsets.ISO_8859_1);

		// save document
		doc.toFile("TestFiles/books.out.xml");

		// load new document (ISO-8859-1)
		Document doc2 = Document.fromFile("TestFiles/books.out.xml");

		// change encoding back to UTF-8
		doc.setEncoding(StandardCharsets.UTF_8);
		doc2.setEncoding(StandardCharsets.UTF_8);

		// documents should be equal
		Assert.assertEquals(doc, doc2);
	}

	@Test
	public void testToFile() throws Exception
	{
		// create document

		Document doc = new Document();
		Element rootElement = new Element("currencies");
		doc.setRootElement(rootElement);

		// CHF

		Element chfElement = new Element("currency", "ccy1");
		rootElement.appendChild(chfElement);

		Element chfName = new Element("name");
		chfName.appendChild(new TextNode("Swiss Franc"));
		chfElement.appendChild(chfName);

		Element chfShort = new Element("shortForm");
		chfShort.appendChild(new TextNode("CHF"));
		chfElement.appendChild(chfShort);

		Element chfConversion = new Element("conversion");
		chfConversion.setAttribute("USD", "1.10");
		chfConversion.setAttribute("EUR", "0.83");
		chfConversion.setAttribute("GBP", "0.69");
		chfElement.appendChild(chfConversion);

		// EUR

		Element eurElement = new Element("currency", "ccy2");
		rootElement.appendChild(eurElement);

		Element eurName = new Element("name");
		eurName.appendChild(new TextNode("Euro"));
		eurElement.appendChild(eurName);

		Element eurShort = new Element("shortForm");
		eurShort.appendChild(new TextNode("EUR (€)"));
		eurElement.appendChild(eurShort);

		Element eurConversion = new Element("conversion");
		eurConversion.setAttribute("USD", "1.33");
		eurConversion.setAttribute("EUR", "1.00");
		eurConversion.setAttribute("GBP", "0.84");
		eurElement.appendChild(eurConversion);

		// USD

		Element usdElement = new Element("currency", "ccy3");
		rootElement.appendChild(usdElement);

		Element usdName = new Element("name");
		usdName.appendChild(new TextNode("US Dollar"));
		usdElement.appendChild(usdName);

		Element usdShort = new Element("shortForm");
		usdShort.appendChild(new TextNode("USD ($)"));
		usdElement.appendChild(usdShort);

		Element usdConversion = new Element("conversion");
		usdConversion.setAttribute("USD", "1.00");
		usdConversion.setAttribute("EUR", "0.75");
		usdConversion.setAttribute("GBP", "0.63");
		usdElement.appendChild(usdConversion);

		// GBP

		Element gbpElement = new Element("currency", "ccy4");
		rootElement.appendChild(gbpElement);

		Element gbpName = new Element("name");
		gbpName.appendChild(new TextNode("Great Britain Pound"));
		gbpElement.appendChild(gbpName);

		Element gbpShort = new Element("shortForm");
		gbpShort.appendChild(new TextNode("GBP (£)"));
		gbpElement.appendChild(gbpShort);

		Element gbpConversion = new Element("conversion");
		gbpConversion.setAttribute("USD", "1.59");
		gbpConversion.setAttribute("EUR", "1.20");
		gbpConversion.setAttribute("GBP", "1.00");
		gbpElement.appendChild(gbpConversion);

		// write to file

		doc.toFile("TestFiles/currencies.out.xml");

		// load file

		Document doc2 = Document.fromFile("TestFiles/currencies.out.xml");

		// verify values

		Assert.assertEquals("0.83", doc2.getRootElement().getElementById("ccy1").getElementByName("conversion").getAttribute("EUR"));
		Assert.assertEquals("1.00", doc2.getRootElement().getElementById("ccy2").getElementByName("conversion").getAttribute("EUR"));
		Assert.assertEquals("0.75", doc2.getRootElement().getElementById("ccy3").getElementByName("conversion").getAttribute("EUR"));
		Assert.assertEquals("1.20", doc2.getRootElement().getElementById("ccy4").getElementByName("conversion").getAttribute("EUR"));
	}

	@Test
	public void testFromW3CDocument() throws Exception
	{
		try
		{
			//load dom
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document w3cDocument = builder.parse(new File("TestFiles/books.xml"));

			// write content to xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(w3cDocument);
			StreamResult result = new StreamResult(new File("TestFiles/books.dom1.out.xml"));
			transformer.transform(source, result);

			// convert W3C document to document
			Document doc = Document.fromW3CDocument(w3cDocument);
			doc.toFile("TestFiles/books.dom2.out.xml");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testToW3CDocument() throws Exception
	{
		try
		{
			//load document
			Document doc = Document.fromFile("TestFiles/books.xml");

			// convert to W3C document
			org.w3c.dom.Document w3cDocument = doc.toW3CDocument();

			// write content to xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(w3cDocument);
			StreamResult result = new StreamResult(new File("TestFiles/books.dom3.out.xml"));
			transformer.transform(source, result);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
