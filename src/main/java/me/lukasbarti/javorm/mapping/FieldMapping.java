package me.lukasbarti.javorm.mapping;

import me.lukasbarti.javorm.Javorm;
import me.lukasbarti.javorm.mapping.PropertyMap;
import me.lukasbarti.javorm.typing.TypeConverters;

import java.sql.ResultSet;

public abstract class FieldMapping {

    protected final String fieldName;
    protected final Class<?> targetType;

    protected FieldMapping(String fieldName, Class<?> targetType) {
        this.fieldName = fieldName;
        this.targetType = targetType;
    }

    public String fieldName() {
        return this.fieldName;
    }

    public abstract Object mapForEntity(Javorm instance, ResultSet resultSet, PropertyMap<?> propertyMap, TypeConverters typeConverters) throws Exception;
    public abstract int getPriority();

}
