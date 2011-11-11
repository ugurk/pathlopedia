package com.pathlopedia.datastore.entity;

import com.pathlopedia.datastore.DatastoreException;

public interface IEditable {
    public boolean isEditable(User user) throws DatastoreException;
}
