package com.pathlopedia.servlet.entity;

import org.bson.types.ObjectId;

public final class ObjectIdResponse {
    public final String id;

    public ObjectIdResponse(ObjectId id) {
        this.id = id.toString();
    }
}
