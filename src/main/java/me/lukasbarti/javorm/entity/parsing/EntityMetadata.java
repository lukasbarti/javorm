package me.lukasbarti.javorm.entity.parsing;

import me.lukasbarti.javorm.mapping.FieldMapping;

import java.util.*;

public class EntityMetadata {

    public String tableName;
    public String key;
    public List<FieldMapping> mappings;

    public EntityMetadata() {
        this.mappings = new ArrayList<>();
    }
}
