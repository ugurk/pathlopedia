package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.*;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdEntity;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class PointAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Create the point.
        Point p = new Point(
                Coordinate.parse(getTrimmedParameter("coordinate")),
                getSessionUser(),
                getTrimmedParameter("title"),
                getTrimmedParameter("text"),
                ShareType.parse(getTrimmedParameter("shareType")));

        // Save the point.
        DatastorePortal.safeSave(p);

        // Pack and return the result.
        return new JSONResponse(0, new ObjectIdEntity(p.getId()));
    }
}
