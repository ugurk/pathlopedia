package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Coordinate;
import com.pathlopedia.datastore.entity.Point;
import com.pathlopedia.datastore.entity.User;
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
        requireLogin();

        // Parse input arguments.
        Coordinate lo = Coordinate.parse(getTrimmedParameter("lo"));
        Coordinate hi = Coordinate.parse(getTrimmedParameter("hi"));

        // Fetch points.
        List<Point> points = DatastorePortal.getDatastore()
                .find(Point.class)
                .filter("visible", true)
                .field("location")
                .within(lo.getLat(), lo.getLng(), hi.getLat(), hi.getLng())
                .asList();

        // Pack and return the fetched points.
        User user = getSessionUser();
        List<PointListItemEntity> itemEntities =
                new ArrayList<PointListItemEntity>(points.size());
        for (Point point : points)
            if (point.isAccessible(user))
                itemEntities.add(new PointListItemEntity(
                        point.getId().toString(),
                        point.getLocation(),
                        point.getTitle()));
        return new JSONResponse(0, itemEntities);
    }
}
