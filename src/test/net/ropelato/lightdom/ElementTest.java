package net.ropelato.lightdom;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ElementTest
{
	private Document doc = null;

	@Before
	public void setUp() throws Exception
	{
		doc = Document.fromFile("TestFiles/books.xml");
	}

	@Test
	public void testGetElementByName() throws Exception
	{
		String price = doc.getRootElement().getElementByName("book/price").getText();

		Assert.assertEquals(price, "44.95");
	}

	@Test
	public void testGetElementsByName() throws Exception
	{
		String price1 = doc.getRootElement().getElementsByName("book/price").get(0).getText();
		String price2 = doc.getRootElement().getElementsByName("book/price").get(1).getText();

		Assert.assertEquals(price1, "44.95");
		Assert.assertEquals(price2, "31.95");
	}

	@Test
	public void testGetFloatAttribute() throws Exception
	{
		double price1 = doc.getRootElement().getElementsByName("book/price").get(0).getTextAsDouble();
		double price2 = doc.getRootElement().getElementsByName("book/price").get(1).getTextAsDouble();

		Assert.assertEquals(price1, 44.95d, Double.MIN_VALUE);
		Assert.assertEquals(price2, 31.95d, Double.MIN_VALUE);
	}

	@Test
	public void testAppendChild() throws Exception
	{
		Document doc2 = Document.fromFile("TestFiles/books.xml");

		// move second book element to the end
		doc2.getRootElement().appendChild(doc2.getRootElement().getElementsByName("book").get(1));

		// check index
		Assert.assertEquals(doc2.getRootElement().getElementsByName("book").get(11).getElementByName("description").getIndex(), "-1,11,5");
	}

	@Test
	public void testGetElementsByQuery() throws Exception
	{
		Element book1Title = doc.getRootElement().getElementByQuery("book[@id = 'bk101' and price > 30]/title");
		Assert.assertEquals(book1Title.getText(), "XML Developer's Guide");
		Assert.assertTrue(book1Title == doc.getRootElement().getElementByName("book").getElementByName("title"));

		Element book2Author = doc.getRootElement().getElementByQuery("book[contains(description, 'battle one another')]/author");
		Assert.assertEquals(book2Author.getText(), "Corets, Eva");
		Assert.assertTrue(book2Author == doc.getRootElement().getElementById("bk105").getElementByName("author"));
	}
}
