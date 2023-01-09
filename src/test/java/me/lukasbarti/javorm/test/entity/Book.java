package me.lukasbarti.javorm.test.entity;

import me.lukasbarti.javorm.entity.DatabaseEntity;
import me.lukasbarti.javorm.entity.parsing.annotation.Key;
import me.lukasbarti.javorm.entity.parsing.annotation.Table;
import me.lukasbarti.javorm.mapping.external.OneToOne;

@Table("test_books")
public class Book implements DatabaseEntity {
    @Key
    public int id;

    public String title;

    @OneToOne(source = "_author_id")
    public Author author;
}