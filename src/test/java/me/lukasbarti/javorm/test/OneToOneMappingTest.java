package me.lukasbarti.javorm.test;

import me.lukasbarti.javorm.Javorm;
import me.lukasbarti.javorm.entity.parsing.annotation.AnnotationEntityParser;
import me.lukasbarti.javorm.test.entity.Author;
import me.lukasbarti.javorm.test.entity.Book;
import me.lukasbarti.javorm.typing.TypeConverters;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OneToOneMappingTest {

    private Connection connection;
    private Javorm javorm;

    @BeforeAll
    public void setUp() throws Exception {
        var properties = new Properties();
        properties.load(BasicMappingTest.class.getResourceAsStream("/database.properties"));

        this.connection = DriverManager.getConnection(properties.getProperty("connection_string"));
        this.javorm = Javorm.forConnection(this.connection, new AnnotationEntityParser(), new TypeConverters().withDefaults());

        this.javorm.parseEntity(Book.class);
        this.javorm.parseEntity(Author.class);
    }

    @AfterAll
    public void tearDown() throws Exception {
        if (this.connection != null)
            this.connection.close();
    }

    @Test
    public void testOneToOneMapping() throws Exception {
        var book = this.javorm.getEntity(Book.class, "SELECT * FROM test_books;");

        Assertions.assertNotNull(book);

        System.out.println("Example book (fetched with statement): " + book.title);
        System.out.println("  -> Author: " + book.author.name);
    }


}
