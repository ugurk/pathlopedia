package com.pathlopedia.datastore.entity;

public enum ShareType {
    PRIVATE,
    FRIENDS,
    PUBLIC;

    public static ShareType parse(String s) {
        if (s == null)
            throw new IllegalArgumentException("NULL 'shareType' field!");
        else if (s.equals("PRIVATE")) return PRIVATE;
        else if (s.equals("FRIENDS")) return FRIENDS;
        else if (s.equals("PUBLIC")) return PUBLIC;
        else
            throw new IllegalArgumentException(
                    "Invalid 'shareType' field: " + s);
    }

    public boolean isAccessible(User objUser, User reqUser) {
        return (objUser.equals(reqUser) ||
                (this != PRIVATE &&
                        (this == PUBLIC || objUser.isFriend(reqUser))));
    }
}
