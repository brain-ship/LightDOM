LightDOM is a lightweight java-based DOM library to process XML documents. Have a look at the examples or at the source code and the test classes to see how LightDOM works.

# Maven dependency

Use the following Maven dependency to use LightDOM in your project:

    <dependency>
        <groupId>io.github.barkbeetle</groupId>
        <artifactId>lightdom</artifactId>
        <version>1.1.4</version>
    </dependency>

# Examples
### Load, modify and save XML file

    Document doc = Document.fromFile("TestFiles/books.xml");
    Element ratingElement = new Element("rating");
    ratingElement.appendChild(new TextNode("8.5"));
    doc.getRootElement().getElementById("bk103").appendChild(ratingElement);
    doc.toFile("TestFiles/books2.xml");

### Create new document from scratch

    // create document
    
    Document doc = new Document();
    Element rootElement = new Element("movies");
    doc.setRootElement(rootElement);
    
    // movie 1
    
    Element movie1 = new Element("movie", "m1");
    
    Element title1 = new Element("title");
    title1.appendChild(new TextNode("The Lord of the Rings: The Fellowship of the Ring"));
    Element director1 = new Element("director");
    director1.appendChild(new TextNode("Peter Jackson"));
    Element released1 = new Element("released");
    released1.appendChild(new TextNode("2001"));
    
    movie1.appendChild(title1);
    movie1.appendChild(director1);
    movie1.appendChild(released1);
    
    // movie 2
    
    Element movie2 = new Element("movie", "m2");
    
    Element title2 = new Element("title");
    title2.appendChild(new TextNode("Avatar"));
    Element director2 = new Element("director");
    director2.appendChild(new TextNode("James Cameron"));
    Element released2 = new Element("released");
    released2.appendChild(new TextNode("2009"));
    
    movie2.appendChild(title2);
    movie2.appendChild(director2);
    movie2.appendChild(released2);
    
    // movie 3
    
    Element movie3 = new Element("movie", "m3");
    
    Element title3 = new Element("title");
    title3.appendChild(new TextNode("Skyfall"));
    Element director3 = new Element("director");
    director3.appendChild(new TextNode("Sam Mendes"));
    Element released3 = new Element("released");
    released3.appendChild(new TextNode("2012"));
    
    movie3.appendChild(title3);
    movie3.appendChild(director3);
    movie3.appendChild(released3);
    
    // save document to file
    
    rootElement.appendChild(movie1);
    rootElement.appendChild(movie2);
    rootElement.appendChild(movie3);
    
    doc.toFile("TestFiles/movies.out.xml");

### Find element with XPath

    Document doc = Document.fromFile("TestFiles/books.xml");
    Element book1Title = doc.getRootElement().getElementByQuery("book[@id = 'bk101' and price > 30]/title");
    Element book2Author = doc.getRootElement().getElementByQuery("book[contains(description, 'battle one another')]/author");

