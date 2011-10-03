package com.pathlopedia.servlet;

import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Point;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class PointDelServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Fetch the point.
        Point point = DatastorePortal.safeGet(
                Point.class, req.getParameter("point"));

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive point!");

        // Validate user is the point owner.
        if (!point.getUser().equals(req.getSession().getAttribute("user")))
            throw new ServletException("Access denied!");

        // Deactivate point.
        point.deactivate();

        // Return success.
        return new JSONResponse(0);
    }
}
