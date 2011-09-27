package com.pathlopedia.ds;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.Mongo;
import com.pathlopedia.ds.entity.*;
import org.bson.types.ObjectId;

public final class DatastorePortal {
    private Datastore ds;
    private static DatastorePortal conn = null;

    public final static String DATABASE_NAME = "pathlopedia";

    public DatastorePortal() throws DatastoreException {
        try {
            Morphia morphia = new Morphia();
            morphia
                .map(Coordinate.class)
                .map(User.class)
                .map(ImageData.class)
                .map(Image.class)
                .map(Attachment.class)
                .map(Comment.class)
                .map(Point.class);
            this.ds = morphia.createDatastore(new Mongo(), DATABASE_NAME);
        } catch (Exception e) {
            throw new DatastoreException("Data source connection failed!", e);
        }
    }

    private static void initialize() throws DatastoreException {
        if (conn == null)
            conn = new DatastorePortal();
    }

    public static Datastore getDatastore() throws DatastoreException {
        initialize();
        return conn.ds;
    }

    public static <T> UpdateResults<T> safeUpdate(T ent, UpdateOperations<T> ops)
            throws DatastoreException {
        initialize();
        UpdateResults<T> res = conn.ds.update(ent, ops);
        if (res.getHadError())
            throw new DatastoreException(res.getError());
        return res;
    }

    public static <T> Key<T> safeSave(T ent) throws DatastoreException {
        initialize();
        Key<T> key = conn.ds.save(ent);
        if (key == null)
            throw new DatastoreException("Couldn't save entity: "+ent);
        return key;
    }

    public static <T> T safeGet(Class<T> clazz, Object id)
            throws DatastoreException {
        initialize();
        if (id instanceof String)
            id = new ObjectId((String) id);
        T ent = conn.ds.get(clazz, id);
        if (ent == null)
            throw new DatastoreException("Couldn't find entity: "+id);
        return ent;
    }
}
