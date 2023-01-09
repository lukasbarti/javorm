package me.lukasbarti.javorm.test.entity;

import me.lukasbarti.javorm.mapping.external.OneToMany;

import java.util.List;

public class AdvancedAuthor extends Author {

    @OneToMany(source = "id", target = "_author_id", type = Book.class)
    public List<Book> books;

}
