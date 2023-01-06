package me.lukasbarti.javorm.mapping;

import me.lukasbarti.javorm.Javorm;
import me.lukasbarti.javorm.typing.TypeConverters;

import java.sql.ResultSet;

public class BasicFieldMapping extends FieldMapping {

    public BasicFieldMapping(String fieldName, Class<?> targetType) {
        super(fieldName, targetType);
    }

    @Override
    public Object mapForEntity(Javorm instance, ResultSet resultSet, PropertyMap<?> propertyMap, TypeConverters typeConverters) throws Exception {
        var result = resultSet.getObject(this.fieldName);

        return typeConverters.convertToType(this.targetType, result);
    }

}
