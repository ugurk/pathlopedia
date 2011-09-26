package com.pathlopedia.servlet.entity;

import org.bson.types.ObjectId;

public final class ObjectIdEntity {
    public final String id;

    public ObjectIdEntity(ObjectId id) {
        this.id = id.toString();
    }
}
