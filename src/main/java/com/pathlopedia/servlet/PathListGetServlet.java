package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.*;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.entity.PathListItemEntity;
import com.pathlopedia.servlet.entity.PointListItemEntity;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PathListGetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();
        Datastore ds = DatastorePortal.getDatastore();

        // Parse input arguments.
        Coordinate lo = Coordinate.parse(getTrimmedParameter("lo"));
        Coordinate hi = Coordinate.parse(getTrimmedParameter("hi"));

        // Fetch corners intersecting with this bounding box.
        List<Corner> corners = ds.find(Corner.class)
                .filter("visible", true)
                .field("location")
                .within(lo.getLat(), lo.getLng(), hi.getLat(), hi.getLng())
                .asList();

        // Get session user.
        User user = getSessionUser();

        // Collect paths referenced by corners.
        Set<Path> paths = new HashSet<Path>();
        for (Corner corner : corners) {
            Path path = corner.getParent().getPath();
            if (path.isVisible() && path.isAccessible(user))
                paths.add(path);
        }

        // Pack paths.
        List<PathListItemEntity> items = new ArrayList<PathListItemEntity>();
        for (Path path : paths) {
            // Pack corners in this path.
            List<Coordinate> pathCorners = new ArrayList<Coordinate>();
            for (Corner corner : path.getCorners())
                pathCorners.add(corner.getLocation());

            // Pack points in this path.
            List<PointListItemEntity> pathPoints =
                    new ArrayList<PointListItemEntity>();
            for (Point point : path.getPoints())
                // Points need a second accessibility check.
                // See PathGetServlet for details.
                if (point.isVisible() && point.isAccessible(user))
                    pathPoints.add(new PointListItemEntity(
                            point.getId().toString(),
                            point.getLocation(),
                            point.getTitle()));

            // Pack this path.
            items.add(new PathListItemEntity(
                    path.getId().toString(),
                    path.getUser().getId().toString(),
                    path.getUser().getName(),
                    path.getTitle(),
                    path.getScore(),
                    pathCorners,
                    pathPoints));
        }

        return new JSONResponse(0, items);
    }
}
