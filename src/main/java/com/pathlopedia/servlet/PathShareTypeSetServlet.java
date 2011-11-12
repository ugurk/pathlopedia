package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Path;
import com.pathlopedia.datastore.entity.Point;
import com.pathlopedia.datastore.entity.ShareType;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.util.Shortcuts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class PathShareTypeSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Validate inputs.
        ShareType shareType = ShareType.parse(getTrimmedParameter("shareType"));
        boolean cascade = Shortcuts.parseBoolean(getTrimmedParameter("cascade"));

        // Fetch the path.
        Path path = DatastorePortal.safeGet(Path.class,
                getTrimmedParameter("path"));

        // Check path visibility.
        if (!path.isVisible())
            throw new ServletException("Inactive path!");

        // Check path accessibility.
        if (!path.isEditable(getSessionUser()))
            throw new ServletException("Access denied!");

        // Update the path.
        DatastorePortal.safeUpdate(path,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Path.class)
                        .set("shareType", shareType)
                        .set("updatedAt", new Date()));

        // Update connected points, if necessary.
        if (cascade)
            DatastorePortal.safeUpdate(
                    DatastorePortal.getDatastore()
                            .find(Point.class)
                            .filter("visible", true)
                            .filter("parent.path", path),
                    DatastorePortal.getDatastore()
                            .createUpdateOperations(Point.class)
                            .set("shareType", shareType));

        // Return success.
        return new JSONResponse(0);
    }
}
