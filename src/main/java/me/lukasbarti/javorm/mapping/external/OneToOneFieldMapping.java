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
        if("".equals(this.annotation.target())) {
            return instance.getEntityByKey(this.targetType, resultSet.getObject(this.annotation.source()));
        } else {
            return instance.getEntityWithCondition(this.targetType, this.annotation.target() + " = ?",resultSet.getObject(this.annotation.source()));
        }
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
