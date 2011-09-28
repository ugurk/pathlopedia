package com.pathlopedia.servlet;

import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Path;
import com.pathlopedia.ds.entity.Point;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public class PathPointAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Fetch the path.
        Path path = DatastorePortal.safeGet(
                Path.class, req.getParameter("path"));

        // Validate the path user.
        if (!path.getUser().equals(req.getSession().getAttribute("user")))
            throw new ServletException("Access denied!");

        // Fetch the point.
        Point point = DatastorePortal.safeGet(
                Point.class, req.getParameter("point"));

        // Check if the point is already included.
        if (path.getPoints().contains(point))
            return new JSONResponse(1,
                    "Point is already included by the path!");

        // Update the point.
        DatastorePortal.safeUpdate(point,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Point.class)
                        .set("path", path));

        // Update the path.
        DatastorePortal.safeUpdate(path,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Path.class)
                        .add("points", point)
                        .set("updatedAt", new Date()));

        // Return success.
        return new JSONResponse(0);
    }
}
