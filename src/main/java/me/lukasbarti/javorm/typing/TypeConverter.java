package me.lukasbarti.javorm.typing;

public interface TypeConverter<T> {

    T convert(Object object);

}
