package me.lukasbarti.javorm.typing;

public class CastConverter<T> implements TypeConverter<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T convert(Object object) {
        return (T) object;
    }

}
