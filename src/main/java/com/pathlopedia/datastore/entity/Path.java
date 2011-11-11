package com.pathlopedia.datastore.entity;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.*;
import com.pathlopedia.datastore.DatastoreException;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.util.Shortcuts;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity("paths")
public class Path implements IParent {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

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
    private List<Corner> corners;

    @Reference(lazy=true)
    private List<Point> points;

    @Reference(lazy=true)
    private List<Comment> comments;

    @SuppressWarnings("unused")
    private Path() {}

    public Path(User user, String title, String text, ShareType shareType)
            throws DatastoreException {
        this.user = user;
        this.title = title;
        this.text = text;
        this.shareType = shareType;
        this.score = 0;
        this.scorers = null;
        this.updatedAt = new Date();
        this.visible = true;
        this.corners = null;
        this.points = null;
        this.comments = null;
        validate();
    }

    @PostLoad
    @PrePersist
    private void validate() throws DatastoreException {
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
            throw new DatastoreException("NULL 'shareType' field!");
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

    public List<Corner> getCorners() {
        if (this.corners == null)
            return new ArrayList<Corner>();
        return this.corners;
    }

    public List<Point> getPoints() {
        if (this.points == null)
            return new ArrayList<Point>();
        return this.points;
    }

    public List<Comment> getComments() {
        if (this.comments == null)
            return new ArrayList<Comment>();
        return this.comments;
    }

    public boolean equals(Object that) {
        return (that instanceof Path && this.id.equals(((Path) that).id));
    }

    public void deactivate() throws DatastoreException {
        // Check if deactivation is necessary.
        if (!this.isVisible()) return;

        // Get datastore handle.
        Datastore ds = DatastorePortal.getDatastore();

        // Deactivate path.
        DatastorePortal.safeUpdate(this,
                ds.createUpdateOperations(Path.class)
                        .set("visible", false));

        // Deactivate corners.
        Corner.deactivate(
                ds.find(Corner.class)
                        .filter("parent.path", this));

        // Deactivate comments.
        Comment.deactivate(
                ds.find(Comment.class)
                        .filter("parent.path", this));

        // Deactivate points.
        for (Point point : this.getPoints())
            point.deactivate();
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
