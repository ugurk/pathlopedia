package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Coordinate;
import com.pathlopedia.datastore.entity.Point;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class PointLocationSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Fetch the point.
        Point point = DatastorePortal.safeGet(Point.class,
                getTrimmedParameter("point"));

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive point!");

        // Check point owner.
        if (!point.getUser().equals(req.getSession().getAttribute("user")))
            throw new ServletException("Access denied!");

        // Construct the new location.
        Coordinate coord = Coordinate.parse(getTrimmedParameter("coordinate"));

        // Update the point.
        DatastorePortal.safeUpdate(point,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Point.class)
                        .set("location", coord)
                        .set("updatedAt", new Date()));

        // Return success.
        return new JSONResponse(0);
    }
}
