package me.lukasbarti.javorm;

import me.lukasbarti.javorm.entity.DatabaseEntity;
import me.lukasbarti.javorm.entity.parsing.EntityMetadata;
import me.lukasbarti.javorm.entity.parsing.EntityParser;
import me.lukasbarti.javorm.mapping.FieldMapping;
import me.lukasbarti.javorm.mapping.PropertyMap;
import me.lukasbarti.javorm.typing.TypeConverters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Javorm {

    private static final int FLAG_EMPTY = 0x00000000;
    private static final int FLAG_KEY = 0x00000001;
    private static final int FLAG_TABLE_NAME = 0x00000010;

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

    private EntityMetadata getMetadataAndCheckFlags(Class<?> entityClass, int entityFlags) {
        if (!this.entityMetadataMap.containsKey(entityClass))
            throw new UnsupportedOperationException("Entity with class " + entityClass.getName() + " has not been parsed yet.");

        var metadata = this.entityMetadataMap.get(entityClass);

        if ((entityFlags & Javorm.FLAG_KEY) == Javorm.FLAG_KEY) {
            Objects.requireNonNull(metadata.key);
        }

        if((entityFlags & Javorm.FLAG_TABLE_NAME) == Javorm.FLAG_TABLE_NAME) {
            Objects.requireNonNull(metadata.tableName);
        }

        return metadata;
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

        return statement.executeQuery();
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
        var entities = this.getEntities(entityClass, query, parameters);

        if (entities.size() >= 1) {
            return entities.get(0);
        }

        throw new NoSuchElementException("No results could be found for the query: " + query + ".");
    }

    public <T> T getEntityWithCondition(Class<T> entityClass, String condition, Object... parameters) throws Exception {
        var metadata = this.getMetadataAndCheckFlags(entityClass, Javorm.FLAG_TABLE_NAME);

        return this.getEntity(entityClass, "SELECT * FROM " + metadata.tableName + " WHERE " + condition + ";", parameters);
    }

    public <T> T getEntityByKey(Class<T> entityClass, Object key) throws Exception {
        var metadata = this.getMetadataAndCheckFlags(entityClass, Javorm.FLAG_TABLE_NAME | Javorm.FLAG_KEY);

        return this.getEntity(entityClass, "SELECT * FROM " + metadata.tableName + " WHERE " + metadata.key + " = ?;", key);
    }

    public <T> List<T> getEntities(Class<T> entityClass, String query, Object... parameters) throws Exception {
        var resultSet = this.executeStatementWithParameters(query, parameters);

        var entities = new ArrayList<T>();

        while (resultSet.next()) {
            var properties = this.executeEntityMappings(entityClass, resultSet);
            var object = entityClass.getConstructor().newInstance();

            properties.applyPropertiesToObject(object);

            entities.add(object);
        }

        return entities;
    }

    public <T> List<T> getEntitiesWithCondition(Class<T> entityClass, String condition, Object... parameters) throws Exception {
        var metadata = this.getMetadataAndCheckFlags(entityClass, Javorm.FLAG_TABLE_NAME);

        return this.getEntities(entityClass, "SELECT * FROM " + metadata.tableName + " WHERE " + condition + ";", parameters);
    }

    public static Javorm forConnection(Connection connection, EntityParser entityParser, TypeConverters typeConverters) {
        return new Javorm(connection, entityParser, typeConverters);
    }

}
