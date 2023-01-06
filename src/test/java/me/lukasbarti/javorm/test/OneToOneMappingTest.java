package me.lukasbarti.javorm.test;

import me.lukasbarti.javorm.Javorm;
import me.lukasbarti.javorm.entity.DatabaseEntity;
import me.lukasbarti.javorm.entity.parsing.annotation.AnnotationEntityParser;
import me.lukasbarti.javorm.entity.parsing.annotation.Key;
import me.lukasbarti.javorm.entity.parsing.annotation.Table;
import me.lukasbarti.javorm.mapping.external.OneToOne;
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
        properties.load(BasicFunctionalityTest.class.getResourceAsStream("/database.properties"));

        this.connection = DriverManager.getConnection(properties.getProperty("connection_string"));
        this.javorm = Javorm.forConnection(connection, new AnnotationEntityParser(), new TypeConverters().withDefaults());

        javorm.parseEntity(Book.class);
        javorm.parseEntity(Author.class);
    }

    @AfterAll
    public void tearDown() throws Exception {
        if (connection != null)
            connection.close();
    }

    @Test
    public void testOneToOneMapping() throws Exception {
        var book = this.javorm.getEntity(Book.class, "SELECT * FROM test_books;");

        Assertions.assertNotNull(book);

        System.out.println("Author of the book: " + book.author.name);
    }

    @Table("test_books")
    public static class Book implements DatabaseEntity {
        @Key
        public int id;
        public String title;
        @OneToOne(mappedBy = "_author_id")
        public Author author;
    }

    @Table("test_authors")
    public static class Author implements DatabaseEntity {
        @Key
        public int id;

        public String name;
    }


}
