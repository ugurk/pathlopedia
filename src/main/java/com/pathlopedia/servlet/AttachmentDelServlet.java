package com.pathlopedia.servlet;

import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Attachment;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class AttachmentDelServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Fetch the attachment.
        Attachment attachment = DatastorePortal.safeGet(Attachment.class,
                req.getParameter("attachment"));

        // Check attachment visibility.
        if (!attachment.isVisible())
            throw new ServletException("Inactive attachment!");

        // Validate that user owns the attachment.
        if (!attachment.getParent().getPoint().getUser()
                .equals(req.getSession().getAttribute("user")))
            throw new ServletException("Access denied!");

        // Deactivate the attachment.
        attachment.deactivate();

        // Return success.
        return new JSONResponse(0);
    }
}
