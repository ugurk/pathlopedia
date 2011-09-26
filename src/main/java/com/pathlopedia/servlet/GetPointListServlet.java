package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Coordinate;
import com.pathlopedia.ds.entity.Point;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.PointListItemEntity;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class GetPointListServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Parse input arguments.
        Coordinate begPoint = new Coordinate(
                Double.parseDouble(req.getParameter("beglat")),
                Double.parseDouble(req.getParameter("beglng")));
        Coordinate endPoint = new Coordinate(
                Double.parseDouble(req.getParameter("endlat")),
                Double.parseDouble(req.getParameter("endlng")));
        Datastore ds = DatastorePortal.getDatastore();

        // Fetch points.
        // TODO: Access permission checks.
        // TODO: Clustering.
        List<Point> points = ds.find(Point.class)
                .filter("user", req.getSession().getAttribute("user"))
                .field("location.lat").lessThan(begPoint.getLat())
                .field("location.lng").lessThan(begPoint.getLng())
                .field("location.lat").greaterThan(endPoint.getLat())
                .field("location.lng").greaterThan(endPoint.getLng())
                .asList();

        // Pack and return the fetched points.
        List<PointListItemEntity> itemEntities =
                new ArrayList<PointListItemEntity>(points.size());
        for (Point point : points)
            itemEntities.add(new PointListItemEntity(
                    point.getId().toString(),
                    point.getLocation(),
                    point.getTitle()));
        return new JSONResponse(0, itemEntities);
    }
}
