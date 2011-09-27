package com.pathlopedia.ds.entity;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.PostLoad;
import com.google.code.morphia.annotations.PrePersist;
import com.pathlopedia.ds.DatastoreException;
import org.bson.types.ObjectId;

@Embedded
public class Parent {
    private ObjectId id;
    private Type type;

    public enum Type { ATTACHMENT, POINT }

    @SuppressWarnings("unused")
    private Parent() {}

    public Parent(ObjectId id, Type type) {
        this.id = id;
        this.type = type;
    }

    @PostLoad
    @PrePersist
    @SuppressWarnings("unused")
    private void validate() throws DatastoreException {
        validateId();
        validateType();
    }

    private void validateId() throws DatastoreException {
        if (this.id == null)
            throw new DatastoreException("NULL 'id' field!");
    }

    public ObjectId getId() {
        return id;
    }

    private void validateType() throws DatastoreException {
        if (this.type == null)
            throw new DatastoreException("NULL 'type' field!");
    }

    public Type getType() {
        return type;
    }
}
