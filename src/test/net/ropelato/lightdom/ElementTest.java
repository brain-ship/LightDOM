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
}
