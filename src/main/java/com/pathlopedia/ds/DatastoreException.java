package com.pathlopedia.ds;

import java.io.IOException;

public class DatastoreException extends IOException {
    private final String message;
    private final Exception exception;

    public DatastoreException(String message) {
        this.message = message;
        this.exception = null;
    }

    public DatastoreException(String message, Exception exception)
            throws NullPointerException {
        if (exception == null)
            throw new NullPointerException();
        this.message = message;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public Exception getException() {
        return exception;
    }
}
