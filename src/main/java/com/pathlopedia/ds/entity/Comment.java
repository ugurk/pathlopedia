package com.pathlopedia.ds.entity;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.*;
import com.pathlopedia.ds.DatastoreException;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity("comments")
public final class Comment {
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

    @SuppressWarnings("unused")
    private Comment() {}

    public Comment(Parent parent, User user, String text)
            throws DatastoreException {
        this.parent = parent;
        this.user = user;
        this.text = text;
        this.score = 0;
        this.scorers = new ArrayList<Key<User>>();
        this.updatedAt = new Date();
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateParent();
        validateUser();
        validateText();
        validateScore();
        validateScorers();
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
                 this.parent.getType() != Parent.Type.POINT))
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

    private void validateScorers() throws DatastoreException {
        if (this.scorers == null)
            throw new DatastoreException("NULL 'scorers' field!");
    }

    public List<Key<User>> getScorers() {
        return this.scorers;
    }

    private void validateUpdatedAt() throws DatastoreException {
        if (this.updatedAt == null)
            throw new DatastoreException("NULL 'updatedAt' field!");
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public boolean equals(Object that) {
        return (that instanceof Comment && this.id.equals(((Comment) that).id));
    }
}
