package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.query.UpdateOperations;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Attachment;
import com.pathlopedia.ds.entity.Parent;
import com.pathlopedia.ds.entity.User;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class AttachmentScoreSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);
        Datastore ds = DatastorePortal.getDatastore();

        // Locate the given attachment.
        Attachment attachment = DatastorePortal.safeGet(
                Attachment.class, req.getParameter("attachment"));

        // Check attachment visibility.
        if (!attachment.isVisible())
            return new JSONResponse(1, "Inactive attachment!");

        // Check if user tries to vote for his/her own entity.
        assert attachment.getParent().getType().equals(Parent.Type.POINT);
        String userId = (String) req.getSession().getAttribute("userId");
        User user = ds.get(User.class, attachment.getParent().getId());
        assert user != null;
        if (user.getId().toString().equals(userId))
            return new JSONResponse(1,
                    "You cannot vote for your own attachment!");

        // Get user key.
        @SuppressWarnings("unchecked")
        Key<User> userKey = (Key<User>) req.getSession().getAttribute("userKey");

        // Check if user had previously scored.
        if (attachment.getScorers().contains(userKey))
            return new JSONResponse(1,
                    "You have already scored this attachment!");

        // Parse user input and create an appropriate update operation set.
        int step = Integer.parseInt(req.getParameter("step"));
        UpdateOperations<Attachment> ops = ds.createUpdateOperations(
                Attachment.class).add("scorers", userKey);
        if (step == 1) ops = ops.inc("score");
        else if (step == -1) ops = ops.dec("score");
        else throw new ServletException("Invalid score step size: "+step);

        // Update last modification time.
        ops = ops.set("updatedAt", new Date());

        // Issue the update operation.
        DatastorePortal.safeUpdate(attachment, ops);

        // Return success.
        return new JSONResponse(0);
    }
}
