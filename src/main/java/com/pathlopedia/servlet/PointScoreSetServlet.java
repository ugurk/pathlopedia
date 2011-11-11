package com.pathlopedia.servlet;

import com.google.code.morphia.query.UpdateOperations;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Point;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class PointScoreSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Locate the given point.
        Point point = DatastorePortal.safeGet(
                Point.class, getTrimmedParameter("point"));

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive point!");

        // Check point scorability.
        if (!point.isScorable(getSessionUser()))
            throw new ServletException("Access denied!");

        // Parse user input and create an appropriate update operation set.
        int step = Integer.parseInt(getTrimmedParameter("step"));
        UpdateOperations<Point> ops = DatastorePortal.getDatastore()
                .createUpdateOperations(Point.class)
                .add("scorers", getSessionUser().getKey());
        if (step == 1) ops = ops.inc("score");
        else if (step == -1) ops = ops.dec("score");
        else throw new ServletException("Invalid score step size: "+step);

        // Update last modification time.
        ops = ops.set("updatedAt", new Date());

        // Issue the update operation.
        DatastorePortal.safeUpdate(point, ops);

        // Return success.
        return new JSONResponse(0);
    }
}
