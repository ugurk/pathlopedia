package com.pathlopedia.datastore.entity;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.*;
import com.google.code.morphia.utils.IndexDirection;
import com.pathlopedia.datastore.DatastoreException;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.util.Shortcuts;
import org.bson.types.ObjectId;

import java.util.*;

@Entity("points")
public class Point implements IParent {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    @Embedded
    private Parent parent;

    @Embedded
    @Indexed(IndexDirection.GEO2D)
    private Coordinate location;

    @Reference(lazy=true)
    private User user;

    private String title;
    private String text;
    private ShareType shareType;
    private int score;
    private List<Key<User>> scorers;
    private Date updatedAt;
    private boolean visible;

    @Reference(lazy=true)
    private List<Comment> comments;

    @Reference(lazy=true)
    private List<Attachment> attachments;

    @SuppressWarnings("unused")
    private Point() {}

    public Point(
            Coordinate location,
            User user,
            String title,
            String text,
            ShareType shareType)
            throws DatastoreException {
        this.parent = null;
        this.location = location;
        this.user = user;
        this.title = title;
        this.text = text;
        this.shareType = shareType;
        this.score = 0;
        this.scorers = null;
        this.updatedAt = new Date();
        this.visible = true;
        this.comments = null;
        this.attachments = null;
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateParent();
        validateLocation();
        validateUser();
        validateTitle(this.title);
        validateText(this.text);
        validateShareType(this.shareType);
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
        if (this.parent != null && this.parent.getType() != Parent.Type.PATH)
            throw new DatastoreException("Invalid parent: "+this.parent);
    }

    public Parent getParent() {
        return this.parent;
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

    public static void validateTitle(String title) throws DatastoreException {
        Shortcuts.validateStringLength(
                "title", title, DatastorePortal.TITLE_MAX_LENGTH, false);
    }

    public String getTitle() {
        return this.title;
    }

    public static void validateText(String text) throws DatastoreException {
        Shortcuts.validateStringLength(
                "text", text, DatastorePortal.TEXT_MAX_LENGTH, true);
    }

    public String getText() {
        return this.text;
    }

    public static void validateShareType(ShareType shareType)
            throws DatastoreException {
        if (shareType == null)
            throw new DatastoreException("NULl 'shareType' field!");
    }

    public ShareType getShareType() {
        return this.shareType;
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

    public List<Comment> getComments() {
        if (this.comments == null)
            return new ArrayList<Comment>();
        return this.comments;
    }

    public List<Attachment> getAttachments() {
        if (this.attachments == null)
            return new ArrayList<Attachment>();
        return this.attachments;
    }

    public boolean equals(Object that) {
        return (that instanceof Point && this.id.equals(((Point) that).id));
    }

    public void deactivate() throws DatastoreException {
        // Check if deactivation is necessary.
        if (!this.isVisible()) return;

        // Get datastore handle.
        Datastore ds = DatastorePortal.getDatastore();

        // Deactivate point.
        DatastorePortal.safeUpdate(this,
                ds.createUpdateOperations(Point.class)
                        .set("visible", false));

        // Deactivate attachments.
        Attachment.deactivate(
                ds.find(Attachment.class)
                        .filter("parent.point", this));

        // Deactivate comments.
        Comment.deactivate(
                ds.find(Comment.class)
                        .filter("parent.point", this));
    }

    public boolean isAccessible(User user) {
        return getShareType().isAccessible(getUser(), user);
    }

    public boolean isEditable(User user) {
        return getUser().equals(user);
    }

    public boolean isScorable(User user) {
        return (!getUser().equals(user) &&
                isAccessible(user) &&
                !getScorers().contains(user.getKey()));
    }
}
