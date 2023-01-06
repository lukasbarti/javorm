package me.lukasbarti.javorm.test;

import me.lukasbarti.javorm.Javorm;
import me.lukasbarti.javorm.entity.parsing.annotation.AnnotationEntityParser;
import me.lukasbarti.javorm.test.entity.AdvancedAuthor;
import me.lukasbarti.javorm.test.entity.Author;
import me.lukasbarti.javorm.test.entity.Book;
import me.lukasbarti.javorm.typing.TypeConverters;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OneToManyMappingTest {

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
        this.javorm.parseEntity(AdvancedAuthor.class);
    }

    @AfterAll
    public void tearDown() throws Exception {
        if (this.connection != null)
            this.connection.close();
    }

    @Test
    public void testOneToManyMapping() throws Exception {
        var author = this.javorm.getEntity(AdvancedAuthor.class, "SELECT * FROM test_authors;");

        Assertions.assertNotNull(author);

        System.out.println("Example author (fetched with statement): " + author.name);
        for (int i = 0; i < author.books.size(); i++) {
            System.out.println("  -> Book " + i + ": " + author.books.get(i).title);
        }

    }

}
