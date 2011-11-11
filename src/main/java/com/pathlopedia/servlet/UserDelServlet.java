package com.pathlopedia.servlet;

import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class UserDelServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Deactivate user.
        getSessionUser().deactivate();

        // Logout user.
        req.getSession().invalidate();

        // Return success.
        return new JSONResponse(0);
    }
}
