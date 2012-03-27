package net.ropelato.lightdom.test;

import net.ropelato.lightdom.Document;

public class Test1
{
	public static void main(String[] args)
	{
		Document doc = Document.fromFile("Test/TestFiles/books.xml");
		doc.toFile("Test/TestFiles/books.out.xml");
	}
}
