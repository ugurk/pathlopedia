package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Point;
import com.pathlopedia.datastore.entity.ShareType;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class PointShareTypeSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Validate input shareType.
        ShareType shareType = ShareType.parse(getTrimmedParameter("shareType"));

        // Fetch the point.
        Point point = DatastorePortal.safeGet(Point.class,
                getTrimmedParameter("path"));

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive path!");

        // Check point accessibility.
        if (!point.isEditable(getSessionUser()))
            throw new ServletException("Access denied!");

        // Update the point.
        DatastorePortal.safeUpdate(point,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Point.class)
                        .set("shareType", shareType)
                        .set("updatedAt", new Date()));

        // Return success.
        return new JSONResponse(0);
    }
}
