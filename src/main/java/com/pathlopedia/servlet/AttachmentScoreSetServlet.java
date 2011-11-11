package com.pathlopedia.servlet;

import com.google.code.morphia.query.UpdateOperations;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Attachment;
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
        requireLogin();

        // Locate the given attachment.
        Attachment attachment = DatastorePortal.safeGet(
                Attachment.class, getTrimmedParameter("attachment"));

        // Check attachment visibility.
        if (!attachment.isVisible())
            throw new ServletException("Inactive attachment!");

        // Check attachment scorability.
        if (!attachment.isScorable(getSessionUser()))
            throw new ServletException("Access denied!");

        // Parse user input and create an appropriate update operation set.
        int step = Integer.parseInt(getTrimmedParameter("step"));
        UpdateOperations<Attachment> ops = DatastorePortal.getDatastore()
                .createUpdateOperations(Attachment.class)
                .add("scorers", getSessionUser().getKey());
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
