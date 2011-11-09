package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.query.UpdateOperations;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Comment;
import com.pathlopedia.datastore.entity.User;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class CommentScoreSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();
        Datastore ds = DatastorePortal.getDatastore();

        // Fetch comment.
        Comment comment = DatastorePortal.safeGet(
                Comment.class, getTrimmedParameter("comment"));

        // Check comment visibility.
        if (!comment.isVisible())
            throw new ServletException("Inactive comment!");

        // Check if user tries to vote for his/her own comment.
        if (comment.getUser().equals(req.getSession().getAttribute("user")))
            throw new ServletException(
                    "You cannot vote for your own comment!");

        // TODO Check comment accessibility.

        // Get user key.
        @SuppressWarnings("unchecked")
        Key<User> userKey =
                (Key<User>) req.getSession().getAttribute("userKey");

        // Check if user had previously scored.
        if (comment.getScorers().contains(userKey))
            throw new ServletException(
                    "You have already scored this comment!");

        // Parse user input and create an appropriate update operation set.
        int step = Integer.parseInt(getTrimmedParameter("step"));
        UpdateOperations<Comment> ops = ds.createUpdateOperations(
                Comment.class).add("scorers", userKey);
        if (step == 1) ops = ops.inc("score");
        else if (step == -1) ops = ops.dec("score");
        else throw new ServletException("Invalid score step size: "+step);

        // Update last modification time.
        ops = ops.set("updatedAt", new Date());

        // Issue the update operation.
        DatastorePortal.safeUpdate(comment, ops);

        // Return success.
        return new JSONResponse(0);
    }
}
