package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Point;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class PointDelServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Fetch the point.
        Point point = DatastorePortal.safeGet(
                Point.class, getTrimmedParameter("point"));

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive point!");

        // Check point accessibility.
        if (!point.isEditable(getSessionUser()))
            throw new ServletException("Access denied!");

        // Deactivate point.
        point.deactivate();

        // Return success.
        return new JSONResponse(0);
    }
}
