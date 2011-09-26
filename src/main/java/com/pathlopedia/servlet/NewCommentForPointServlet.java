package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Comment;
import com.pathlopedia.ds.entity.Parent;
import com.pathlopedia.ds.entity.Point;
import com.pathlopedia.ds.entity.User;
import com.pathlopedia.servlet.entity.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdResponse;
import com.pathlopedia.servlet.entity.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

/**
 * User: Volkan YAZICI <volkan.yazici@gmail.com>
 * Date: 9/22/11
 * Time: 7:30 PM
 */
public final class NewCommentForPointServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);
        Datastore ds = DatastorePortal.getDatastore();

        // Locate the point.
        Point point = DatastorePortal.safeGet(
                Point.class, req.getParameter("point"));

        // Create and save the comment.
        Comment comment = new Comment(
                new Parent(point.getId(), Parent.Type.POINT),
                (User) req.getSession().getAttribute("user"),
                req.getParameter("text"));
        DatastorePortal.safeSave(comment);

        // Update the point.
        DatastorePortal.safeUpdate(point,
                ds.createUpdateOperations(Point.class)
                        .add("comments", comment)
                        .set("updatedAt", new Date()));

        // Return success with the created comment id.
        return new JSONResponse(0, new ObjectIdResponse(comment.getId()));
    }
}
