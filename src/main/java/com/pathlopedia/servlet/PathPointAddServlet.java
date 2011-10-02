package com.pathlopedia.servlet;

import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Path;
import com.pathlopedia.ds.entity.Point;
import com.pathlopedia.ds.entity.User;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class PathPointAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Fetch the path.
        Path path = DatastorePortal.safeGet(
                Path.class, req.getParameter("path"));

        // Check path visibility.
        if (!path.isVisible())
            throw new ServletException("Inactive path!");

        // Get current user.
        User user = (User) req.getSession().getAttribute("user");

        // Validate the path user.
        if (!path.getUser().equals(user))
            throw new ServletException("Access denied!");

        // Fetch the point.
        Point point = DatastorePortal.safeGet(
                Point.class, req.getParameter("point"));

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive point!");

        // Validate the point user.
        if (!point.getUser().equals(user))
            throw new ServletException("Access denied!");

        // Check if the point is already included.
        if (path.getPoints().contains(point))
            throw new ServletException(
                    "Point is already included by the path!");

        // Update the point.
        DatastorePortal.safeUpdate(point,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Point.class)
                        .set("updatedAt", new Date())
                        .set("path", path));

        // Update the path.
        DatastorePortal.safeUpdate(path,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Path.class)
                        .set("updatedAt", new Date())
                        .add("points", point));

        // Return success.
        return new JSONResponse(0);
    }
}
