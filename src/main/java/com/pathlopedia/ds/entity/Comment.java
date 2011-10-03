package com.pathlopedia.ds.entity;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.*;
import com.google.code.morphia.query.Query;
import com.pathlopedia.ds.DatastoreException;
import com.pathlopedia.ds.DatastorePortal;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity("comments")
public class Comment {
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
        validateText();
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

    @SuppressWarnings("unused")
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

    private void validateText() throws DatastoreException {
        if (this.text == null || this.text.length() == 0)
            throw new DatastoreException("NULL (or empty) 'text' field!");
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
}
