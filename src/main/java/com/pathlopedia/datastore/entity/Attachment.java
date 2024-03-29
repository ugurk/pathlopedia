package com.pathlopedia.datastore.entity;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.*;
import com.google.code.morphia.query.Query;
import com.pathlopedia.datastore.DatastoreException;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.util.Shortcuts;
import org.bson.types.ObjectId;

import java.util.*;

@Entity("attachments")
public class Attachment implements IParent {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    @Embedded
    private Parent parent;

    private String text;
    private int score;
    private List<Key<User>> scorers;
    private Date updatedAt;
    private boolean visible;
    private Type type;

    @Embedded
    private Image image;

    @Reference(lazy=true)
    private List<Comment> comments;

    public enum Type { IMAGE }

    @SuppressWarnings("unused")
    private Attachment() {}

    public Attachment(
            Parent parent,
            String text,
            Image image)
            throws DatastoreException {
        this.parent = parent;
        this.text = text;
        this.score = 0;
        this.scorers = null;
        this.updatedAt = new Date();
        this.visible = true;
        this.type = Type.IMAGE;
        this.image = image;
        this.comments = null;
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateParent();
        validateText(this.text);
        validateScore();
        validateUpdatedAt();
        validateType();
        validateImage();
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
            throw new DatastoreException("Invalid parent: " + this.parent);
    }

    public Parent getParent() {
        return this.parent;
    }

    public static void validateText(String text) throws DatastoreException {
        Shortcuts.validateStringLength(
                "text", text, DatastorePortal.TEXT_MAX_LENGTH, true);
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

    public List<Comment> getComments() {
        if (this.comments == null)
            return new ArrayList<Comment>();
        return this.comments;
    }

    public boolean equals(Object that) {
        return (that instanceof Attachment &&
                this.id.equals(((Attachment) that).id));
    }

    public void deactivate() throws DatastoreException {
        if (this.isVisible())
            DatastorePortal.safeUpdate(this,
                    DatastorePortal.getDatastore()
                            .createUpdateOperations(Attachment.class)
                            .set("visible", false));
    }

    public static void deactivate(Query<Attachment> query)
            throws DatastoreException {
        DatastorePortal.safeUpdate(
                query.filter("visible", true),
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Attachment.class)
                        .set("visible", false));
    }

    public boolean isAccessible(User user) throws DatastoreException {
        return getParent().getObject().isAccessible(user);
    }

    public boolean isEditable(User user) throws DatastoreException {
        return getParent().getObject().isEditable(user);
    }

    public boolean isScorable(User user) throws DatastoreException {
        return getParent().getObject().isScorable(user);
    }
}
