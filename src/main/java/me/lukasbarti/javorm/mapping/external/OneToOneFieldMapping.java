package me.lukasbarti.javorm.mapping.external;

import me.lukasbarti.javorm.Javorm;
import me.lukasbarti.javorm.mapping.FieldMapping;
import me.lukasbarti.javorm.mapping.PropertyMap;
import me.lukasbarti.javorm.typing.TypeConverters;

import java.sql.ResultSet;

public class OneToOneFieldMapping extends FieldMapping {

    private final OneToOne annotation;

    public OneToOneFieldMapping(String fieldName, Class<?> targetType, OneToOne annotation) {
        super(fieldName, targetType);

        this.annotation = annotation;
    }

    @Override
    public Object mapForEntity(Javorm instance, ResultSet resultSet, PropertyMap<?> propertyMap, TypeConverters typeConverters) throws Exception {
        if("".equals(annotation.targetColumn())) {
            return instance.getEntityByKey(targetType, resultSet.getObject(annotation.mappedBy()));
        } else {
            return instance.getEntityWithCondition(targetType, annotation.targetColumn() + " = ?",resultSet.getObject(annotation.mappedBy()));
        }

    }

    @Override
    public int getPriority() {
        return 1;
    }
}
