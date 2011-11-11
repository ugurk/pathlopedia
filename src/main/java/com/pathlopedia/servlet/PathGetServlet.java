package com.pathlopedia.servlet;

import com.google.code.morphia.Key;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.*;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.entity.CommentEntity;
import com.pathlopedia.servlet.entity.PathEntity;
import com.pathlopedia.servlet.entity.PointListItemEntity;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PathGetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Fetch the path.
        Path path = DatastorePortal.safeGet(
                Path.class, getTrimmedParameter("path"));

        // Check path visibility.
        if (!path.isVisible())
            throw new ServletException("Inactive path!");

        // Get session user.
        User user = getSessionUser();

        // Check attachment accessibility.
        if (!path.isAccessible(user))
            throw new ServletException("Access denied!");

        // Collect corners.
        List<Coordinate> corners = new ArrayList<Coordinate>();
        for (Corner corner : path.getCorners())
            if (corner.isVisible())
                corners.add(corner.getLocation());

        // Collect points.
        List<PointListItemEntity> points = new ArrayList<PointListItemEntity>();
        for (Point point : path.getPoints())
            // Pay attention that, a point can be inaccessible, where the path
            // the point is associated to is accessible. Hence, we need to
            // check point accessibility separately.
            if (point.isVisible() && point.isAccessible(user))
                points.add(new PointListItemEntity(
                        point.getId().toString(),
                        point.getLocation(),
                        point.getTitle()));

        // Collect comments.
        List<CommentEntity> comments = new ArrayList<CommentEntity>();
        for (Comment comment : path.getComments())
            if (comment.isVisible())
                comments.add(new CommentEntity(
                        comment.getId().toString(),
                        comment.getUser().getId().toString(),
                        comment.getUser().getName(),
                        comment.getText(),
                        comment.getScore(),
                        comment.isScorable(user),
                        comment.getUpdatedAt()));

        // Pack and return the result.
        return new JSONResponse(0, new PathEntity(
                path.getId().toString(),
                path.getUser().getId().toString(),
                path.getUser().getName(),
                path.getTitle(),
                path.getText(),
                path.getScore(),
                path.isScorable(user),
                path.getUpdatedAt(),
                corners,
                points,
                comments));
    }

}
