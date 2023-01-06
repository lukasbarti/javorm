package me.lukasbarti.javorm.test;

import me.lukasbarti.javorm.Javorm;
import me.lukasbarti.javorm.entity.DatabaseEntity;
import me.lukasbarti.javorm.entity.parser.annotation.AnnotationEntityParser;
import me.lukasbarti.javorm.entity.parser.annotation.Key;
import me.lukasbarti.javorm.entity.parser.annotation.Table;
import me.lukasbarti.javorm.typing.TypeConverters;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicFunctionalityTest {

    private Connection connection;
    private Javorm javorm;

    @BeforeAll
    public void setUp() throws Exception {
        var properties = new Properties();
        properties.load(BasicFunctionalityTest.class.getResourceAsStream("/database.properties"));

        this.connection = DriverManager.getConnection(properties.getProperty("connection_string"));
        this.javorm = Javorm.forConnection(connection, new AnnotationEntityParser(), new TypeConverters().withDefaults());

        javorm.parseEntity(TestEntity.class);
    }

    @AfterAll
    public void tearDown() throws Exception {
        if (connection != null)
            connection.close();
    }

    @Test
    public void testBasicMappingWithNativeStatement() throws Exception {
        var entity = javorm.getEntity(TestEntity.class, "SELECT * FROM test_entities;");

        Assertions.assertNotNull(entity);
        System.out.println("Test entity (native): " + entity.test);
    }

    @Test
    public void testBasicMappingWithKey() throws Exception {
        var entity = javorm.getEntityByKey(TestEntity.class, 1);

        Assertions.assertNotNull(entity);
        System.out.println("Test entity (parsed with key): " + entity.test);
    }

    @Table("test_entities")
    public static class TestEntity implements DatabaseEntity {
        @Key
        public int id;
        public String test;
    }


}
