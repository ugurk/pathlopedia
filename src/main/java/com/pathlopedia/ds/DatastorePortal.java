package com.pathlopedia.ds;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.Mongo;
import com.pathlopedia.ds.entity.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public final class DatastorePortal {
    private Datastore ds;
    private static DatastorePortal conn = null;

    public DatastorePortal() throws DatastoreException {
        try {
            Morphia morphia = new Morphia();
            morphia
                    .map(Parent.class)
                    .map(Coordinate.class)
                    .map(User.class)
                    .map(ImageData.class)
                    .map(Image.class)
                    .map(Attachment.class)
                    .map(Comment.class)
                    .map(Point.class)
                    .map(Corner.class)
                    .map(Path.class);
            String dbname = System.getProperty("custom.db.name");
            if (dbname == null)
                throw new DatastoreException(
                        "custom.db.name property is missing!");
            this.ds = morphia.createDatastore(new Mongo(), dbname);
        } catch (Exception e) {
            throw new DatastoreException(
                    "Data source connection failed: "+e.getMessage());
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

    public static <T> UpdateResults<T> safeUpdate(
            Query<T> q,
            UpdateOperations<T> ops)
            throws DatastoreException {
        initialize();
        UpdateResults<T> res = conn.ds.update(q, ops);
        if (res.getHadError())
            throw new DatastoreException(res.getError());
        return res;
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

    public static <T> Iterable<Key<T>> safeSave(Iterable<T> ents)
            throws DatastoreException {
        initialize();
        Iterable<Key<T>> keys = conn.ds.save(ents);
        if (keys == null)
            throw new DatastoreException("Couldn't save entities: "+ents);
        return keys;
    }

    public static <T> T safeGet(Class<T> clazz, Object id)
            throws DatastoreException {
        initialize();
        // Try to convert String's to ObjectId's
        if (id instanceof String)
            id = new ObjectId((String) id);

        // Issue the query.
        T ent = conn.ds.get(clazz, id);
        if (ent == null)
            throw new DatastoreException("Couldn't find entity: "+id);
        return ent;
    }

    public static <T, V> Query<T> safeGet(Class<T> clazz, Iterable<V> ids)
            throws DatastoreException {
        initialize();
        Query<T> query;
        if (ids.iterator().hasNext() && ids.iterator().next() instanceof String) {
            // Try to convert String's to ObjectId's
            List<ObjectId> newIds = new ArrayList<ObjectId>();
            for (Object id : ids)
                newIds.add(new ObjectId((String) id));

            // Issue the query using new ids.
            query = conn.ds.get(clazz, newIds);
        }
        else
        // Issue the query as is.
            query = conn.ds.get(clazz, ids);

        // Check and return the result.
        if (query == null)
            throw new DatastoreException("Couldn't find entitites: "+ids);
        return query;
    }
}
