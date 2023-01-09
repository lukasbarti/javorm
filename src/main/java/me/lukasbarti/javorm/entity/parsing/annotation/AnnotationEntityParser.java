package me.lukasbarti.javorm.entity.parsing.annotation;

import me.lukasbarti.javorm.entity.DatabaseEntity;
import me.lukasbarti.javorm.entity.parsing.EntityMetadata;
import me.lukasbarti.javorm.entity.parsing.EntityParser;
import me.lukasbarti.javorm.mapping.basic.BasicFieldMapping;
import me.lukasbarti.javorm.mapping.external.OneToMany;
import me.lukasbarti.javorm.mapping.external.OneToManyFieldMapping;
import me.lukasbarti.javorm.mapping.external.OneToOne;
import me.lukasbarti.javorm.mapping.external.OneToOneFieldMapping;

import java.lang.reflect.Field;

public class AnnotationEntityParser implements EntityParser {

    @Override
    public EntityMetadata parseEntity(Class<? extends DatabaseEntity> databaseEntity) {
        var metadata = new EntityMetadata();

        if (databaseEntity.isAnnotationPresent(Table.class)) {
            metadata.tableName = databaseEntity.getAnnotation(Table.class).value();
        }

        for (Field field : databaseEntity.getFields()) {
            if (field.isAnnotationPresent(OneToOne.class)) {
                metadata.mappings.add(new OneToOneFieldMapping(field.getName(), field.getType(), field.getAnnotation(OneToOne.class)));
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                metadata.mappings.add(new OneToManyFieldMapping(field.getName(), field.getType(), field.getAnnotation(OneToMany.class)));
            } else {
                if (field.isAnnotationPresent(Key.class)) {
                    metadata.key = field.getName();
                }
                metadata.mappings.add(new BasicFieldMapping(field.getName(), field.getType()));
            }
        }

        return metadata;
    }

}
