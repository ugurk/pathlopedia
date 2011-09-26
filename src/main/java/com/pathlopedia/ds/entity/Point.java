package com.pathlopedia.ds.entity;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.*;
import com.pathlopedia.ds.DatastoreException;
import org.bson.types.ObjectId;

import java.util.*;

@Entity("points")
public final class Point {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    @Embedded
    private Coordinate location;

    @Reference(lazy=true)
    private User user;

    private String title;
    private String text;
    private int score;
    private List<Key<User>> scorers;
    private Date updatedAt;

    @Reference(lazy=true)
    private List<Comment> comments;

    @Reference(lazy=true)
    private List<Attachment> attachments;

    @Transient
    public final int MIN_TITLE_LENGTH = 1;

    @Transient
    public final int MAX_TITLE_LENGTH = 128;

    @SuppressWarnings("unused")
    private Point() {}

    public Point(Coordinate location, User user, String title, String text)
            throws DatastoreException {
        this.location = location;
        this.user = user;
        this.title = title;
        this.text = text;
        this.score = 0;
        this.scorers = new ArrayList<Key<User>>();
        this.updatedAt = new Date();
        this.comments = new ArrayList<Comment>();
        this.attachments = new ArrayList<Attachment>();
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateLocation();
        validateUser();
        validateTitle();
        validateText();
        validateScore();
        validateScorers();
        validateUpdatedAt();
        validateComments();
        validateAttachments();
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

    private void validateLocation() throws DatastoreException {
        if (this.location == null)
            throw new DatastoreException("NULL 'location' field!");
    }

    public Coordinate getLocation() {
        return this.location;
    }

    private void validateUser() throws DatastoreException {
        if (this.user == null)
            throw new DatastoreException("NULL 'user' field!");
    }

    public User getUser() {
        return this.user;
    }

    private void validateTitle() throws DatastoreException {
        if (this.title == null ||
                this.title.length() < MIN_TITLE_LENGTH ||
                this.title.length() > MAX_TITLE_LENGTH)
            throw new DatastoreException(
                    "'title' field ('"+this.title+"') must be between "+
                    MIN_TITLE_LENGTH+" and "+MAX_TITLE_LENGTH+" characters.");
    }

    public String getTitle() {
        return this.title;
    }

    private void validateText() throws DatastoreException {
        if (this.text == null)
            throw new DatastoreException("NULL 'text' field!");
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

    private void validateComments() throws DatastoreException {
        if (this.comments == null)
            throw new DatastoreException("NULL 'comments' field!");
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    private void validateAttachments() throws DatastoreException {
        if (this.attachments == null)
            throw new DatastoreException("NULL 'attachments' field!");
    }

    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    public boolean equals(Object that) {
        return (that instanceof Point && this.id.equals(((Point) that).id));
    }
}
