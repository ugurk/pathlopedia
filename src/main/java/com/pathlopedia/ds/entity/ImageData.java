package com.pathlopedia.ds.entity;

import com.google.code.morphia.annotations.*;
import com.pathlopedia.ds.DatastoreException;
import org.bson.types.ObjectId;

@Entity("imagedatas")
public final class ImageData {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    @Embedded
    private Parent parent;

    private int width;
    private int height;
    private byte[] bytes;

    @SuppressWarnings("unused")
    private ImageData() {}

    public ImageData(Parent parent, int width, int height, byte[] bytes)
            throws DatastoreException {
        this.parent = parent;
        this.width = width;
        this.height = height;
        this.bytes = bytes;
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateParent();
        validateWidth();
        validateHeight();
        validateBytes();
    }

    @PostLoad
    @SuppressWarnings("unused")
    private void validateId() throws DatastoreException {
        if (this.id == null)
            throw new DatastoreException("NULL 'id' field!");
    }

    public ObjectId getId() {
        return this.id;
    }

    private void validateParent() throws DatastoreException {
        if (this.parent == null ||
                this.parent.getType() != Parent.Type.ATTACHMENT)
            throw new DatastoreException("Invalid parent: "+this.parent);
    }

    public Parent getParent() {
        return this.parent;
    }

    private void validateWidth() throws DatastoreException {
        if (this.width < 1)
            throw new DatastoreException(
                    "Width ("+this.width+") cannot be smaller than 1.");
    }

    public int getWidth() {
        return this.width;
    }

    private void validateHeight() throws DatastoreException {
        if (this.height < 1)
            throw new DatastoreException(
                    "Height ("+this.height+") cannot be smaller than 1.");
    }

    public int getHeight() {
        return this.height;
    }

    private void validateBytes() throws DatastoreException {
        if (this.bytes == null)
            throw new DatastoreException("NULL 'bytes' field!");
    }

    public byte[] getBytes() {
        return this.bytes;
    }
}
