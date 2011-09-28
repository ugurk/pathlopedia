package com.pathlopedia.ds.entity;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.*;
import com.google.code.morphia.utils.IndexDirection;
import com.pathlopedia.ds.DatastoreException;
import org.bson.types.ObjectId;

import java.util.*;

@Entity("points")
public class Point {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    @Embedded
    @Indexed(IndexDirection.GEO2D)
    private Coordinate location;

    @Reference(lazy=true)
    private User user;

    private String title;
    private String text;
    private int score;
    @SuppressWarnings("unused")
    private List<Key<User>> scorers;
    @Reference(lazy=true)
    @SuppressWarnings("unused")
    private Path path;
    private Date updatedAt;

    @Reference(lazy=true)
    @SuppressWarnings("unused")
    private List<Comment> comments;

    @Reference(lazy=true)
    @SuppressWarnings("unused")
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
        this.updatedAt = new Date();
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateLocation();
        validateUser();
        validateTitle();
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
        return this.scorers;
    }

    public Path getPath() {
        return path;
    }

    private void validateUpdatedAt() throws DatastoreException {
        if (this.updatedAt == null)
            throw new DatastoreException("NULL 'updatedAt' field!");
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    public boolean equals(Object that) {
        return (that instanceof Point && this.id.equals(((Point) that).id));
    }
}
