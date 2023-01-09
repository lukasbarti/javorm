package me.lukasbarti.javorm.mapping.external;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {

    String source();
    Class<?> type();
    String target();

}
