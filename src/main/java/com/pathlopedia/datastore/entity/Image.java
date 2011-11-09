package com.pathlopedia.datastore.entity;

import com.google.code.morphia.annotations.*;
import com.pathlopedia.datastore.DatastoreException;

@Embedded
public class Image {
    @Reference(lazy=true)
    private ImageData large;

    @Reference(lazy=true)
    private ImageData small;

    @SuppressWarnings("unused")
    private Image() {}

    public Image(ImageData large, ImageData small) throws DatastoreException {
        this.large = large;
        this.small = small;
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateLarge();
        validateSmall();
    }

    private void validateLarge() throws DatastoreException {
        if (this.large == null)
            throw new DatastoreException("NULL 'large' field!");
    }

    public ImageData getLarge() {
        return this.large;
    }

    private void validateSmall() throws DatastoreException {
        if (this.small == null)
            throw new DatastoreException("NULL 'small' field!");
    }

    public ImageData getSmall() {
        return this.small;
    }
}
