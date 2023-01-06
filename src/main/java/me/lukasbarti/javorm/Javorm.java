package me.lukasbarti.javorm;

import me.lukasbarti.javorm.entity.DatabaseEntity;
import me.lukasbarti.javorm.mapping.FieldMapping;
import me.lukasbarti.javorm.mapping.PropertyMap;
import me.lukasbarti.javorm.entity.parsing.EntityMetadata;
import me.lukasbarti.javorm.entity.parsing.EntityParser;
import me.lukasbarti.javorm.typing.TypeConverters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Javorm {

    private final Connection connection;
    private final Map<Class<? extends DatabaseEntity>, EntityMetadata> entityMetadataMap;
    private final EntityParser entityParser;
    private final TypeConverters typeConverters;

    private Javorm(Connection connection, EntityParser entityParser, TypeConverters typeConverters) {
        this.connection = connection;
        this.entityParser = entityParser;
        this.typeConverters = typeConverters;

        this.entityMetadataMap = new HashMap<>();
    }

    public Javorm parseEntity(Class<? extends DatabaseEntity> entityClass) {
        var parsedEntity = this.entityParser.parseEntity(entityClass);
        parsedEntity.mappings.sort(Comparator.comparingInt(FieldMapping::getPriority));

        this.entityMetadataMap.put(entityClass, parsedEntity);

        return this;
    }

    private PreparedStatement prepareStatementWithParameters(String query, Object... parameters) throws SQLException {
        var statement = this.connection.prepareStatement(query);
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }

        return statement;
    }

    private ResultSet executeStatementWithParameters(String query, Object... parameters) throws SQLException {
        var statement = this.prepareStatementWithParameters(query, parameters);
        statement.executeQuery();

        var resultSet = statement.executeQuery();

        if(!resultSet.next())
            throw new NoSuchElementException("No results could be found for query: \"" + query + "\".");

        return resultSet;
    }

    private <T> PropertyMap<T> executeEntityMappings(Class<T> entityClass, ResultSet resultSet) throws Exception {
        var properties = PropertyMap.createPropertyMapForEntity(entityClass);
        var entityMetadata = this.entityMetadataMap.get(entityClass);
        for (FieldMapping entityMapping : entityMetadata.mappings) {
            var mappedValue = entityMapping.mapForEntity(this, resultSet, properties, this.typeConverters);

            properties.put(entityMapping.fieldName(), mappedValue);
        }

        return properties;
    }

    public <T> T getEntity(Class<T> entityClass, String query, Object... parameters) throws Exception {
        if(!this.entityMetadataMap.containsKey(entityClass))
            throw new UnsupportedOperationException("Entity with class " + entityClass.getName() + " has not been parsed yet.");

        var resultSet = this.executeStatementWithParameters(query, parameters);
        var properties = this.executeEntityMappings(entityClass, resultSet);

        var object = entityClass.getConstructor().newInstance();
        properties.applyPropertiesToObject(object);

        return object;
    }

    public <T> T getEntityWithCondition(Class<T> entityClass, String condition, Object... parameters) throws Exception {
        if(!this.entityMetadataMap.containsKey(entityClass))
            throw new UnsupportedOperationException("Entity with class " + entityClass.getName() + " has not been parsed yet.");

        var metadata = this.entityMetadataMap.get(entityClass);
        if(metadata.tableName == null)
            throw new UnsupportedOperationException("Entity with class " + entityClass.getName() + " has does not possess a table name.");

        return this.getEntity(entityClass, "SELECT * FROM " + metadata.tableName + " WHERE " + condition + ";", parameters);
    }

    public <T> T getEntityByKey(Class<T> entityClass, Object key) throws Exception {
        if(!this.entityMetadataMap.containsKey(entityClass))
            throw new UnsupportedOperationException("Entity with class " + entityClass.getName() + " has not been parsed yet.");

        var metadata = this.entityMetadataMap.get(entityClass);
        if(metadata.primaryKey == null)
            throw new UnsupportedOperationException("Entity with class " + entityClass.getName() + " has does not possess a key.");

        return this.getEntityWithCondition(entityClass, metadata.primaryKey + " = ?", key);
    }

    public static Javorm forConnection(Connection connection, EntityParser entityParser, TypeConverters typeConverters) {
        return new Javorm(connection, entityParser, typeConverters);
    }

}
