package com.pathlopedia.servlet;

import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Coordinate;
import com.pathlopedia.ds.entity.Corner;
import com.pathlopedia.ds.entity.Path;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PathCornerSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Fetch given path.
        Path path = DatastorePortal.safeGet(
                Path.class, req.getParameter("path"));

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

        // Delete existing corners.
        List<ObjectId> ids = new ArrayList<ObjectId>();
        for (Corner corner : path.getCorners())
            ids.add(corner.getId());
        DatastorePortal.safeDelete(DatastorePortal.safeGet(Corner.class, ids));

        // Save new corners.
        DatastorePortal.safeSave(corners);

        // Update the path.
        DatastorePortal.safeUpdate(path,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Path.class)
                        .set("corners", corners));

        return new JSONResponse(0);
    }
}
