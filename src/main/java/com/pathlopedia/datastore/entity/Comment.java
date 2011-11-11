package com.pathlopedia.datastore.entity;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.*;
import com.google.code.morphia.query.Query;
import com.pathlopedia.datastore.DatastoreException;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.util.Shortcuts;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity("comments")
public class Comment implements IEditable, IScorable {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    @Embedded
    private Parent parent;

    @Reference(lazy=true)
    private User user;

    private String text;
    private int score;
    private List<Key<User>> scorers;
    private Date updatedAt;
    private boolean visible;

    @SuppressWarnings("unused")
    private Comment() {}

    public Comment(Parent parent, User user, String text)
            throws DatastoreException {
        this.parent = parent;
        this.user = user;
        this.text = text;
        this.score = 0;
        this.scorers = null;
        this.updatedAt = new Date();
        this.visible = true;
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateParent();
        validateUser();
        validateText(this.text);
        validateScore();
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

    private void validateParent() throws DatastoreException {
        if (this.parent == null ||
                (this.parent.getType() != Parent.Type.ATTACHMENT &&
                 this.parent.getType() != Parent.Type.POINT &&
                 this.parent.getType() != Parent.Type.PATH))
            throw new DatastoreException("Invalid parent: "+this.parent);
    }

    public Parent getParent() {
        return this.parent;
    }

    private void validateUser() throws DatastoreException {
        if (this.user == null)
            throw new DatastoreException("NULL 'user' field!");
    }

    public User getUser() {
        return this.user;
    }

    public static void validateText(String text) throws DatastoreException {
        Shortcuts.validateStringLength(
                "text", text, DatastorePortal.COMMENT_MAX_LENGTH, false);
    }

    public String getText() {
        return this.text;
    }

    private void validateScore() throws DatastoreException {
        if (this.score < 0)
            throw new DatastoreException("Negative 'score' field!");
    }

    public int getScore() {
        return this.score;
    }

    public List<Key<User>> getScorers() {
        if (this.scorers == null)
            return new ArrayList<Key<User>>();
        return this.scorers;
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
        return (that instanceof Comment && this.id.equals(((Comment) that).id));
    }

    public void deactivate() throws DatastoreException {
        if (this.isVisible())
            DatastorePortal.safeUpdate(this,
                    DatastorePortal.getDatastore()
                            .createUpdateOperations(Comment.class)
                            .set("visible", false));
    }

    public static void deactivate(Query<Comment> query)
            throws DatastoreException {
        DatastorePortal.safeUpdate(
                query.filter("visible", true),
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Comment.class)
                        .set("visible", false));
    }

    public boolean isEditable(User user) throws DatastoreException {
        return (getUser().equals(user) ||
                getParent().getObject().isEditable(user));
    }

    public boolean isScorable(User user) throws DatastoreException {
        return (!getUser().equals(user) &&
                getParent().getObject().isAccessible(user) &&
                !getScorers().contains(user.getKey()));
    }
}
