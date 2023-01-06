package me.lukasbarti.javorm.entity.parser;

import me.lukasbarti.javorm.mapping.FieldMapping;

import java.util.HashSet;
import java.util.Set;

public class EntityMetadata {

    public String tableName;
    public String primaryKey;
    public Set<FieldMapping> mappings;

    public EntityMetadata() {
        this.mappings = new HashSet<>();
    }
}
