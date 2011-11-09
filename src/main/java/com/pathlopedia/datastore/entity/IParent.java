package com.pathlopedia.datastore.entity;

import com.pathlopedia.datastore.DatastoreException;

public interface IParent {
    public User getUser() throws DatastoreException;
}
