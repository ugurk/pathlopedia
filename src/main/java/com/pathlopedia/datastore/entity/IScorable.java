package com.pathlopedia.datastore.entity;

import com.pathlopedia.datastore.DatastoreException;

public interface IScorable {
    public boolean isScorable(User user) throws DatastoreException;
}
