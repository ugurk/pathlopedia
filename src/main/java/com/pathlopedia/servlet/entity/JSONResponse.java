package com.pathlopedia.servlet.entity;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class JSONResponse implements WritableResponse {
    public final int status;
    public final String message;
    public final Object data;

    public JSONResponse(int status) {
        this.status = status;
        this.message = null;
        this.data = null;
    }

    public JSONResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public JSONResponse(int status, Object data) {
        this.status = status;
        this.message = null;
        this.data = data;
    }

    public void write(HttpServletResponse res) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig()
                .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        res.setContentType("text/json");
        mapper.writeValue(res.getOutputStream(), this);
    }
}
