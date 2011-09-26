package com.pathlopedia.ds.entity;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.*;
import com.pathlopedia.ds.DatastoreException;
import org.bson.types.ObjectId;

import java.util.*;

@Entity("attachments")
public final class Attachment {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    @Embedded
    private Parent parent;

    private String text;
    private int score;
    private List<Key<User>> scorers;
    private Date createdAt;
    private Type type;

    @Embedded
    private Image image;

    @Reference
    private List<Comment> comments;

    public enum Type { IMAGE }

    @SuppressWarnings("unused")
    private Attachment() {}

    public Attachment(Parent parent, String text, Image image)
            throws DatastoreException {
        this.parent = parent;
        this.text = text;
        this.score = 0;
        this.scorers = new ArrayList<Key<User>>();
        this.createdAt = new Date();
        this.type = Type.IMAGE;
        this.image = image;
        this.comments = new ArrayList<Comment>();
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateParent();
        validateText();
        validateScore();
        validateScorers();
        validateCreatedAt();
        validateType();
        validateImage();
        validateComments();
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
        if (this.parent == null || this.parent.getType() != Parent.Type.POINT)
            throw new DatastoreException("Invalid parent: "+this.parent);
    }

    public Parent getParent() {
        return this.parent;
    }

    private void validateText() throws DatastoreException {
        if (this.text == null)
            throw new DatastoreException("NULL 'text' field!");
    }

    public String getText() {
        return this.text;
    }

    private void validateCreatedAt() throws DatastoreException {
        if (this.createdAt == null)
            throw new DatastoreException("NULL 'createdAt' field!");
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

    public Date getCreatedAt() {
        return this.createdAt;
    }

    private void validateType() throws DatastoreException {
        if (this.type == null)
            throw new DatastoreException("NULL 'type' field!");
    }

    public Type getType() {
        return this.type;
    }

    private void validateImage() throws DatastoreException {
        if (this.type == Type.IMAGE && this.image == null)
            throw new DatastoreException("NULL 'image' field!");
    }

    public Image getImage() throws DatastoreException {
        if (this.type != Type.IMAGE)
            throw new DatastoreException("Not of type image!");
        return this.image;
    }

    private void validateComments() throws DatastoreException {
        if (this.comments == null)
            throw new DatastoreException("NULL 'comments' field!");
    }

    public List<Comment> getComments() {
        return this.comments;
    }
}
