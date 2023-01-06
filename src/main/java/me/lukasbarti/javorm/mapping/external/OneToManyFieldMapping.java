package me.lukasbarti.javorm.mapping.external;

import me.lukasbarti.javorm.Javorm;
import me.lukasbarti.javorm.mapping.FieldMapping;
import me.lukasbarti.javorm.mapping.PropertyMap;
import me.lukasbarti.javorm.typing.TypeConverters;

import java.sql.ResultSet;

public class OneToManyFieldMapping extends FieldMapping {

    private final OneToMany annotation;

    public OneToManyFieldMapping(String fieldName, Class<?> targetType, OneToMany annotation) {
        super(fieldName, targetType);

        this.annotation = annotation;
    }

    @Override
    public Object mapForEntity(Javorm instance, ResultSet resultSet, PropertyMap<?> propertyMap, TypeConverters typeConverters) throws Exception {
        return instance.getEntitiesWithCondition(this.annotation.type(), this.annotation.targetColumn() + " = ?", resultSet.getObject(this.annotation.mappedBy()));
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
