package com.pathlopedia.datastore.entity;

import com.pathlopedia.datastore.DatastoreException;

public interface IAccessible {
    public boolean isAccessible(User user) throws DatastoreException;
}
