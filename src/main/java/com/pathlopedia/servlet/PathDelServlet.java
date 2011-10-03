package com.pathlopedia.servlet;

import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.*;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class PathDelServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Fetch the path.
        Path path = DatastorePortal.safeGet(
                Path.class, req.getParameter("path"));

        // Check path visibility.
        if (!path.isVisible())
            throw new ServletException("Inactive path!");

        // Check path owner.
        if (!path.getUser().equals(req.getSession().getAttribute("user")))
            throw new ServletException("Access denied!");

        // Deactivate path.
        path.deactivate();

        // Return success.
        return new JSONResponse(0);
    }
}
