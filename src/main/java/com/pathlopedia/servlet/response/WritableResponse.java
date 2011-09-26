package com.pathlopedia.servlet.response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface WritableResponse {
    public void write(HttpServletResponse res) throws IOException;
}
