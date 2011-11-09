package com.pathlopedia.datastore.entity;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.*;
import com.pathlopedia.datastore.DatastoreException;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.util.Shortcuts;
import org.apache.commons.validator.EmailValidator;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@Entity("users")
public class User {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    private Type type;
    private String name;
    private String email;
    private Date updatedAt;
    private boolean visible;

    public enum Type { FACEBOOK, GOOGLE }

    @SuppressWarnings("unused")
    private User() {}

    public User(Type type, String name, String email)
            throws DatastoreException {
        this.type = type;
        this.name = name;
        this.email = email;
        this.updatedAt = new Date();
        this.visible = true;
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateType();
        validateName(this.name);
        validateEmail();
        validateUpdatedAt();
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

    private void validateType() throws DatastoreException {
        if (this.type == null)
            throw new DatastoreException("NULL 'type' field!");
    }

    public static Type parseType(String type)
            throws IllegalArgumentException {
        if (type == null)
            throw new IllegalArgumentException("NULL 'type' field!");
        if (type.equals("FACEBOOK")) return Type.FACEBOOK;
        else if (type.equals("GOOGLE")) return Type.GOOGLE;
        else throw new IllegalArgumentException("Invalid user type: "+type);
    }

    public Type getType() {
        return this.type;
    }

    public static void validateName(String name) throws DatastoreException {
        Shortcuts.validateStringLength(
                "name", name,
                DatastorePortal.NAME_MIN_LENGTH,
                DatastorePortal.NAME_MAX_LENGTH,
                false);
    }

    public String getName() {
        return this.name;
    }

    private void validateEmail() throws DatastoreException {
        if (this.email == null) {
            Shortcuts.validateStringLength(
                    "email", this.email,
                    DatastorePortal.MAIL_MAX_LENGTH, true);
            if (!EmailValidator.getInstance().isValid(this.email))
                throw new DatastoreException(
                        "Invalid e-mail address: " + this.email);
        }
    }

    public String getEmail() {
        return this.email;
    }

    private void validateUpdatedAt() throws DatastoreException {
        if (this.updatedAt == null)
            throw new DatastoreException("NULL 'updatedAt' field!");
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean equals(Object that) {
        return (that instanceof User && this.id.equals(((User) that).id));
    }

    public void deactivate() throws DatastoreException {
        // Check if deactivation is necessary.
        if (!this.isVisible()) return;

        // Get datastore handle.
        Datastore ds = DatastorePortal.getDatastore();

        // Deactivate user.
        DatastorePortal.safeUpdate(this,
                ds.createUpdateOperations(User.class)
                        .set("visible", false));

        // Deactivate paths.
        List<Path> paths = ds.find(Path.class)
                .filter("visible", true)
                .filter("user", this)
                .asList();
        for (Path path : paths)
            path.deactivate();

        // Deactivate points.
        List<Point> points = ds.find(Point.class)
                .filter("visible", true)
                .filter("user", this)
                .asList();
        for (Point point : points)
            point.deactivate();

        // Deactivate comments.
        Comment.deactivate(ds.find(Comment.class).filter("user", this));
    }
}
