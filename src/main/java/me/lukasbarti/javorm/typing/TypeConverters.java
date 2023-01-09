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
        this.converterMap.put(typeClass, converter);
        return this;
    }

    public TypeConverters withDefaults() {
        this.converterMap.put(String.class, new CastConverter<>());
        this.converterMap.put(int.class, new CastConverter<>());
        this.converterMap.put(long.class, new CastConverter<>());
        this.converterMap.put(double.class, new CastConverter<>());
        this.converterMap.put(float.class, new CastConverter<>());
        this.converterMap.put(boolean.class, new CastConverter<>());
        this.converterMap.put(Date.class, object -> {
            java.sql.Date date = (java.sql.Date) object;
            return new Date(date.getTime());
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T convertToType(Class<T> typeClass, Object object) throws NoSuchElementException {
        if (this.converterMap.containsKey(typeClass))
            return (T) this.converterMap.get(typeClass).convert(object);

        throw new NoSuchElementException("No type conversion for class " + typeClass.getName() + " could be found.");
    }

}
