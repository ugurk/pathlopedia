package com.pathlopedia.datastore.entity;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.*;
import com.pathlopedia.datastore.DatastoreException;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.util.Shortcuts;
import org.apache.commons.validator.EmailValidator;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity("users")
public class User {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    private static Key<User> key = null;

    private String name;
    private String email;
    private Date updatedAt;
    private boolean visible;
    private List<Key<User>> friends;

    @SuppressWarnings("unused")
    private User() {}

    public User(String name, String email) throws DatastoreException {
        this.name = name;
        this.email = email;
        this.updatedAt = new Date();
        this.visible = true;
        this.friends = null;
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
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

    /**
     * Returns the user key. Key is cached in a static variable, hence, updates
     * on the object are not propagated back. Use at your own risk.
     *
     * @return User key.
     */
    public Key<User> getKey() {
        if (key == null)
            key = new Key<User>(User.class, getId());
        return key;
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

    public List<Key<User>> getFriends() {
        if (this.friends == null)
            return new ArrayList<Key<User>>();
        return this.friends;
    }

    public boolean isFriend(User user) {
        return getFriends().contains(user.getKey());
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
