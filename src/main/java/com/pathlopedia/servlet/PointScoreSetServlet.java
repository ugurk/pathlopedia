package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.query.UpdateOperations;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Point;
import com.pathlopedia.datastore.entity.User;
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
        Datastore ds = DatastorePortal.getDatastore();

        // Locate the given point.
        Point point = DatastorePortal.safeGet(
                Point.class, getTrimmedParameter("point"));

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive point!");

        // Check if user tries to vote for his/her own point.
        if (point.getUser().equals(req.getSession().getAttribute("user")))
            throw new ServletException("You cannot vote for your own point!");

        // TODO Check point accessibility.

        // Get user key.
        @SuppressWarnings("unchecked")
        Key<User> userKey =
                (Key<User>) req.getSession().getAttribute("userKey");

        // Check if user had previously voted.
        if (point.getScorers().contains(userKey))
            throw new ServletException("You have already scored this point!");

        // Parse user input and create an appropriate update operation set.
        int step = Integer.parseInt(getTrimmedParameter("step"));
        UpdateOperations<Point> ops = ds.createUpdateOperations(
                Point.class).add("scorers", userKey);
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
