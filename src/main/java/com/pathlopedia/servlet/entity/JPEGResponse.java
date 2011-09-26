package com.pathlopedia.servlet.entity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public final class JPEGResponse implements WritableResponse {
    private final byte[] data;
    private final Date createdAt;
    private final boolean hasError;
    private final int errorCode;

    public JPEGResponse(byte[] data, Date createdAt) {
        this.data = data;
        this.createdAt = createdAt;
        this.hasError = false;
        this.errorCode = 0;
    }

    public JPEGResponse(int errorCode) {
        this.data = null;
        this.createdAt = null;
        this.hasError = true;
        this.errorCode = errorCode;
    }

    public void write(HttpServletResponse res) throws IOException {
        if (this.hasError)
            res.sendError(errorCode);
        else {
            res.setContentType("image/jpeg");
            res.setDateHeader("Last-Modified", createdAt.getTime());
            res.setContentLength(data.length);
            res.getOutputStream().write(data);
        }
    }
}
