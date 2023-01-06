package me.lukasbarti.javorm.entity.parser;

import me.lukasbarti.javorm.entity.DatabaseEntity;

public interface EntityParser {


    EntityMetadata parseEntity(Class<? extends DatabaseEntity> databaseEntity);

}
