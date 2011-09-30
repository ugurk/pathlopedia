package com.pathlopedia.ds.entity;

import com.google.code.morphia.annotations.*;
import com.google.code.morphia.utils.IndexDirection;
import com.pathlopedia.ds.DatastoreException;
import org.bson.types.ObjectId;

@Entity("corners")
public class Corner {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    @Embedded
    private Parent parent;

    private boolean visible;

    @Embedded
    @Indexed(IndexDirection.GEO2D)
    private Coordinate location;

    @SuppressWarnings("unused")
    public Corner() {}

    public Corner(Parent parent, Coordinate location) throws DatastoreException {
        this.parent = parent;
        this.visible = true;
        this.location = location;
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateParent();
        validateLocation();
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
        if (this.parent == null || this.parent.getType() != Parent.Type.PATH)
            throw new DatastoreException("Invalid parent: "+this.parent);
    }

    public Parent getParent() {
        return this.parent;
    }

    public boolean isVisible() {
        return this.visible;
    }

    private void validateLocation() throws DatastoreException {
        if (this.location == null)
            throw new DatastoreException("NULL 'location' field!");
    }

    public Coordinate getLocation() {
        return location;
    }
}
