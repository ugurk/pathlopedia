package com.pathlopedia.ds.entity;

import com.google.code.morphia.annotations.*;
import com.pathlopedia.ds.DatastoreException;
import org.apache.commons.validator.EmailValidator;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity("users")
public class User {
    @Id
    @SuppressWarnings("unused")
    private ObjectId id;

    private Type type;
    private String name;
    private String email;
    private Date updatedAt;
    private boolean visible;

    public enum Type { FACEBOOK, GOOGLE }

    @Transient
    public static final int MIN_NAME_LENGTH = 3;

    @SuppressWarnings("unused")
    public User() {}

    public User(Type type, String name, String email)
            throws DatastoreException {
        this.type = type;
        this.name = name;
        this.email = email;
        this.updatedAt = new Date();
        this.visible = true;
        validate();
    }

    @PrePersist
    @PostLoad
    private void validate() throws DatastoreException {
        validateType();
        validateName();
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

    private void validateType() throws DatastoreException {
        if (this.type == null)
            throw new DatastoreException("NULL 'type' field!");
    }

    public static Type parseType(String type)
            throws IllegalArgumentException {
        if (type == null)
            throw new IllegalArgumentException("NULL 'type' field!");
        if (type.equals("FACEBOOK")) return Type.FACEBOOK;
        else if (type.equals("GOOGLE")) return Type.GOOGLE;
        else throw new IllegalArgumentException("Invalid user type: "+type);
    }

    public Type getType() {
        return this.type;
    }

    private void validateName() throws DatastoreException {
        if (this.name == null || this.name.length() <= MIN_NAME_LENGTH)
            throw new DatastoreException(
                    "Name field ('"+this.name+"') must "+
                    "be equal to or greater than "+MIN_NAME_LENGTH+
                    " characters!");
    }

    public String getName() {
        return this.name;
    }

    private void validateEmail() throws DatastoreException {
        if (this.email == null || !EmailValidator.getInstance().isValid(this.email))
            throw new DatastoreException(
                    "Invalid e-mail address: "+this.email);
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

    public boolean equals(Object that) {
        return (that instanceof User && this.id.equals(((User) that).id));
    }
}
