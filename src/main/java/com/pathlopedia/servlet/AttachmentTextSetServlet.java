package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Attachment;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class AttachmentTextSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Validate given text.
        String text = getTrimmedParameter("text");
        Attachment.validateText(text);

        // Fetch the attachment.
        Attachment attachment = DatastorePortal.safeGet(Attachment.class,
                getTrimmedParameter("attachment"));

        // Check attachment visibility.
        if (!attachment.isVisible())
            throw new ServletException("Inactive attachment!");

        // Check attachment owner.
        if (!attachment.isEditable(getSessionUser()))
            throw new ServletException("Access denied!");

        // Update the attachment.
        DatastorePortal.safeUpdate(attachment,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Attachment.class)
                        .set("text", text)
                        .set("updatedAt", new Date()));

        // Return success.
        return new JSONResponse(0);
    }
}
