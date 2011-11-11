package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Attachment;
import com.pathlopedia.datastore.entity.ImageData;
import com.pathlopedia.servlet.response.JPEGResponse;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class AttachmentImageGetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Fetch the attachment.
        Attachment attachment = DatastorePortal.getDatastore().get(
                Attachment.class, getTrimmedParameter("attachment"));
        if (attachment == null || attachment.getType() != Attachment.Type.IMAGE)
            return JPEGErrorResponse();

        // Check attachment visibility.
        if (!attachment.isVisible())
            return JPEGErrorResponse();

        // Check attachment accessibility.
        if (!attachment.isAccessible(getSessionUser()))
            return JPEGErrorResponse();

        // Parse requested image size.
        String size = getTrimmedParameter("imageSize");
        if (size == null) return JPEGErrorResponse();
        ImageData imageData;
        if (size.equals("large")) imageData = attachment.getImage().getLarge();
        else if (size.equals("small")) imageData = attachment.getImage().getSmall();
        else return JPEGErrorResponse();

        // Pack and return the result.
        return new JPEGResponse(imageData.getBytes(), attachment.getUpdatedAt());
    }

    private WritableResponse JPEGErrorResponse() {
        return new JPEGResponse(HttpServletResponse.SC_NOT_FOUND);
    }
}
