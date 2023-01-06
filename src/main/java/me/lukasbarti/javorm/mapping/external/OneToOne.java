package me.lukasbarti.javorm.mapping.external;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OneToOne {

    String mappedBy();
    String targetColumn() default "";

}
