package me.lukasbarti.javorm.mapping;

import me.lukasbarti.javorm.entity.DatabaseEntity;

import java.util.HashMap;

public class PropertyMap<T extends DatabaseEntity> extends HashMap<String, Object> {

    private final Class<T> typeClass;

    private PropertyMap(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    public void applyPropertiesToObject(T object) throws ReflectiveOperationException {
        for (String fieldName : this.keySet()) {
            var field = typeClass.getField(fieldName);

            field.set(object, this.get(fieldName));
        }
    }

    public static <T extends DatabaseEntity> PropertyMap<T> createPropertyMapForEntity(Class<T> entityClass) {
        return new PropertyMap<>(entityClass);
    }

}
