package me.lukasbarti.javorm.entity.parser.annotation;

import me.lukasbarti.javorm.entity.DatabaseEntity;
import me.lukasbarti.javorm.entity.parser.EntityMetadata;
import me.lukasbarti.javorm.entity.parser.EntityParser;
import me.lukasbarti.javorm.mapping.BasicFieldMapping;

import java.lang.reflect.Field;

public class AnnotationEntityParser implements EntityParser {

    @Override
    public EntityMetadata parseEntity(Class<? extends DatabaseEntity> databaseEntity) {
        var metadata = new EntityMetadata();

        if (databaseEntity.isAnnotationPresent(Table.class)) {
            metadata.tableName = databaseEntity.getAnnotation(Table.class).value();
        }

        for (Field field : databaseEntity.getFields()) {
            if (field.getAnnotations().length == 0 || field.isAnnotationPresent(Key.class)) {
                if(field.isAnnotationPresent(Key.class)) {
                    metadata.primaryKey = field.getName();
                }
                metadata.mappings.add(new BasicFieldMapping(field.getName(), field.getType()));
            } else {
                System.out.println("No mapping for: " + databaseEntity.getName() + "." + field.getName());
            }
        }

        return metadata;
    }

}
