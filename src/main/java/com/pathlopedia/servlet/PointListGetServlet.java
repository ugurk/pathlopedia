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

public final class PointListGetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);
        Datastore ds = DatastorePortal.getDatastore();

        // Parse input arguments.
        Coordinate lo = new Coordinate(
                Double.parseDouble(req.getParameter("lolat")),
                Double.parseDouble(req.getParameter("lolng")));
        Coordinate hi = new Coordinate(
                Double.parseDouble(req.getParameter("hilat")),
                Double.parseDouble(req.getParameter("hilng")));

        // TODO Check point accessibilities.

        // Fetch points.
        List<Point> points = ds.find(Point.class)
                .filter("visible", true)
                .field("location")
                .within(lo.getLat(), lo.getLng(), hi.getLat(), hi.getLng())
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
