package com.pathlopedia.ds.entity;

import com.google.code.morphia.annotations.*;
import com.pathlopedia.ds.DatastoreException;

@Embedded
public final class Image {
    // TODO: Image sizes should be reachable (i.e., embedded) within this class.

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
