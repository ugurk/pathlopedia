package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Coordinate;
import com.pathlopedia.ds.entity.Corner;
import com.pathlopedia.ds.entity.Path;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class PathCornerSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Fetch given path.
        Path path = DatastorePortal.safeGet(
                Path.class, req.getParameter("path"));

        // Check path visibility.
        if (!path.isVisible())
            return new JSONResponse(1, "Inactive path!");

        // Parse input corners.
        String[] coordPairs = req.getParameterValues("corners");
        List<Corner> corners = new ArrayList<Corner>();
        for (String coordPair : coordPairs) {
            String[] coord = coordPair.split(",");
            if (coord.length != 2)
                throw new ServletException(
                        "Invalid coordinate pair: "+coordPair);
            corners.add(new Corner(
                    new Coordinate(
                            Double.parseDouble(coord[0]),
                            Double.parseDouble(coord[1])),
                    path));
        }

        // Deactivate existing corners.
        Datastore ds = DatastorePortal.getDatastore();
        DatastorePortal.safeUpdate(
                ds.find(Corner.class).filter("path", path),
                ds.createUpdateOperations(Corner.class).set("visible", false));

        // Save new corners.
        DatastorePortal.safeSave(corners);

        // Update the path.
        DatastorePortal.safeUpdate(path,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Path.class)
                        .set("updatedAt", new Date())
                        .set("corners", corners));

        // Return success.
        return new JSONResponse(0);
    }
}
