package com.pathlopedia.datastore.entity;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.PostLoad;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.annotations.Reference;
import com.pathlopedia.datastore.DatastoreException;

@Embedded
public class Parent {
    private Type type;

    @Reference(lazy=true)
    private Attachment attachment;

    @Reference(lazy=true)
    private Path path;

    @Reference(lazy=true)
    private Point point;

    public enum Type { ATTACHMENT, PATH, POINT }

    @SuppressWarnings("unused")
    private Parent() {}

    public Parent(Attachment attachment) throws DatastoreException {
        this.type = Type.ATTACHMENT;
        this.attachment = attachment;
        this.path = null;
        this.point = null;
        validate();
    }

    public Parent(Path path) throws DatastoreException {
        this.type = Type.PATH;
        this.attachment = null;
        this.path = path;
        this.point = null;
        validate();
    }

    public Parent(Point point) throws DatastoreException {
        this.type = Type.POINT;
        this.attachment = null;
        this.path = null;
        this.point = point;
        validate();
    }

    @PostLoad
    @PrePersist
    private void validate() throws DatastoreException {
        validateType();
        validateAttachment();
        validatePath();
        validatePoint();
    }

    private void validateType() throws DatastoreException {
        if (this.type == null)
            throw new DatastoreException("NULL 'type' field!");
    }

    public Type getType() {
        return this.type;
    }

    private void validateAttachment() throws DatastoreException {
        if (this.type == Type.ATTACHMENT && this.attachment == null)
            throw new DatastoreException("NULL 'attachment' field!");
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    private void validatePath() throws DatastoreException {
        if (this.type == Type.PATH && this.path == null)
            throw new DatastoreException("NULL 'path' field!");
    }

    public Path getPath() {
        return this.path;
    }

    private void validatePoint() throws DatastoreException {
        if (this.type == Type.POINT && this.point == null)
            throw new DatastoreException("NULL 'point' field!");
    }

    public Point getPoint() {
        return this.point;
    }

    public IParent getObject() throws DatastoreException {
        switch (this.type) {
            case ATTACHMENT: return this.attachment;
            case POINT: return this.point;
            case PATH: return this.path;
            default: throw new DatastoreException("Unknown parent object!");
        }
    }
}
