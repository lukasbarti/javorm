package me.lukasbarti.javorm.typing;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class TypeConverters {

    private final Map<Class<?>, TypeConverter<?>> converterMap;

    public TypeConverters() {
        this.converterMap = new HashMap<>();
    }

    public <T> TypeConverters addConverter(Class<T> typeClass, TypeConverter<T> converter) {
        converterMap.put(typeClass, converter);
        return this;
    }

    public TypeConverters withDefaults() {
        converterMap.put(String.class, new CastConverter<>());
        converterMap.put(int.class, new CastConverter<>());
        converterMap.put(long.class, new CastConverter<>());
        converterMap.put(double.class, new CastConverter<>());
        converterMap.put(float.class, new CastConverter<>());
        converterMap.put(boolean.class, new CastConverter<>());
        converterMap.put(Date.class, object -> {
            java.sql.Date date = (java.sql.Date) object;
            return new Date(date.getTime());
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T convertToType(Class<T> typeClass, Object object) throws NoSuchElementException {
        if (converterMap.containsKey(typeClass))
            return (T) converterMap.get(typeClass).convert(object);

        throw new NoSuchElementException("No type conversion for class " + typeClass.getName() + " could be found.");
    }

}
