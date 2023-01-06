package me.lukasbarti.javorm.test.entity;

import me.lukasbarti.javorm.entity.DatabaseEntity;
import me.lukasbarti.javorm.entity.parsing.annotation.Key;
import me.lukasbarti.javorm.entity.parsing.annotation.Table;

@Table("test_authors")
public class Author implements DatabaseEntity {
    @Key
    public int id;

    public String name;
}