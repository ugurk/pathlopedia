package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Attachment;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class AttachmentDelServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Fetch the attachment.
        Attachment attachment = DatastorePortal.safeGet(Attachment.class,
                getTrimmedParameter("attachment"));

        // Check attachment visibility.
        if (!attachment.isVisible())
            throw new ServletException("Inactive attachment!");

        // Check attachment accessibility.
        if (!attachment.isEditable(getSessionUser()))
            throw new ServletException("Access denied!");

        // Deactivate the attachment.
        attachment.deactivate();

        // Return success.
        return new JSONResponse(0);
    }
}
