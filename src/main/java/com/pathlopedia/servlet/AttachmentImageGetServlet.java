package com.pathlopedia.servlet;

import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Attachment;
import com.pathlopedia.ds.entity.ImageData;
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
        requireLogin(req);

        // Fetch the attachment.
        Attachment attachment = DatastorePortal.getDatastore().get(
                Attachment.class, req.getParameter("attachment"));
        if (attachment == null || attachment.getType() != Attachment.Type.IMAGE)
            return new JPEGResponse(HttpServletResponse.SC_NOT_FOUND);

        // Parse requested image size.
        String size = req.getParameter("imageSize");
        ImageData imageData;
        if (size.equals("large")) imageData = attachment.getImage().getLarge();
        else if (size.equals("small")) imageData = attachment.getImage().getSmall();
        else return new JPEGResponse(HttpServletResponse.SC_NOT_FOUND);

        // Pack and return the result.
        return new JPEGResponse(imageData.getBytes(), attachment.getUpdatedAt());
    }
}
