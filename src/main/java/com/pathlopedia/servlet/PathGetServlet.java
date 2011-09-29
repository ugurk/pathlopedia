package com.pathlopedia.servlet;

import com.google.code.morphia.Key;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.*;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.entity.CommentEntity;
import com.pathlopedia.servlet.entity.PathEntity;
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
        requireLogin(req);

        // Fetch the path.
        Path path = DatastorePortal.safeGet(
                Path.class, req.getParameter("path"));

        // Check path visibility.
        if (!path.isVisible())
            return new JSONResponse(1, "Inactive path!");

        // Get user key.
        @SuppressWarnings("unchecked")
        Key<User> userKey = (Key<User>) req.getSession().getAttribute("userKey");

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
                        comment.getScorers().contains(userKey),
                        comment.getUpdatedAt()));

        // Pack and return the result.
        return new JSONResponse(0, new PathEntity(
                path.getId().toString(),
                path.getUser().getId().toString(),
                path.getUser().getName(),
                path.getTitle(),
                path.getText(),
                path.getScore(),
                path.getScorers().contains(userKey),
                path.getUpdatedAt(),
                comments));
    }

}
