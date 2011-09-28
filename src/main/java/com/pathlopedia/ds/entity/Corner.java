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
    @Indexed(IndexDirection.GEO2D)
    private Coordinate location;

    @Reference(lazy=true)
    @SuppressWarnings("unused")
    private Path path;

    @SuppressWarnings("unused")
    public Corner() {}

    public Corner(Coordinate location, Path path) throws DatastoreException {
        this.location = location;
        this.path = path;
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateLocation();
        validatePath();
    }

    @PostLoad
    @SuppressWarnings("unused")
    private void validateId() throws DatastoreException {
        if (this.id == null)
            throw new DatastoreException("NULL 'id' field!");
    }

    public ObjectId getId() {
        return id;
    }

    private void validateLocation() throws DatastoreException {
        if (this.location == null)
            throw new DatastoreException("NULL 'location' field!");
    }

    public Coordinate getLocation() {
        return location;
    }

    private void validatePath() throws DatastoreException {
        if (this.path == null)
            throw new DatastoreException("NULL 'path' field!");
    }

    public Path getPath() {
        return path;
    }
}
