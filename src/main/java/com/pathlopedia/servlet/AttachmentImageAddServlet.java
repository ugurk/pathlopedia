package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.*;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdEntity;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AttachmentImageAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);
        Datastore ds = DatastorePortal.getDatastore();

        // Parse multipart content.
        if (!ServletFileUpload.isMultipartContent(req))
            throw new ServletException("No multipart content!");
        Map<String,FileItem> fields = parseMultipartContent(req);

        // Locate the point.
        Point point = DatastorePortal.safeGet(
                Point.class, fields.get("point").getString());

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive point!");

        // Check if we are the owner of this point.
        if (!point.getUser().equals(req.getSession().getAttribute("user")))
            throw new ServletException(
                    "User doesn't have required privileges!");

        // Read delivered image and resize it to appropriate sizes.
        BufferedImage image = ImageIO.read(
                fields.get("image").getInputStream());
        ImageData largeImage = resizeImage(image, 600, 400);
        ImageData smallImage = resizeImage(image, 100, 100);

        // Create a new image attachment.
        Attachment attachment = new Attachment(
                new Parent(point),
                fields.get("text").getString(),
                new Image(largeImage, smallImage));

        // Save created images and attachment.
        DatastorePortal.safeSave(largeImage);
        DatastorePortal.safeSave(smallImage);
        DatastorePortal.safeSave(attachment);
        DatastorePortal.safeUpdate(point,
                ds.createUpdateOperations(Point.class)
                        .add("attachments", attachment)
                        .set("updatedAt", new Date()));
        return new JSONResponse(0, new ObjectIdEntity(attachment.getId()));
    }

    private Map<String,FileItem> parseMultipartContent(HttpServletRequest req)
            throws IOException, ServletException {
        // Check if there exists a multipart content.
        if (!ServletFileUpload.isMultipartContent(req))
            throw new ServletException("No multipart content!");

        // Create a disk file item factory for multipart contents.
        ServletFileUpload upload = new ServletFileUpload(
                new DiskFileItemFactory());

        // Try to parse multipart contents.
        try {
            Map<String,FileItem> fields = new HashMap<String, FileItem>();
            @SuppressWarnings("unchecked")
            List<FileItem> items = upload.parseRequest(req);
            if (!items.isEmpty())
                for (FileItem item : items)
                    fields.put(item.getFieldName(), item);
            return fields;
        } catch (FileUploadException e) {
            throw new IOException("FileUploadException: "+e.getMessage());
        }
    }

    private ImageData resizeImage(
            BufferedImage image,
            int maxWidth,
            int maxHeight)
            throws IOException {
        int x = image.getWidth();
        int y = image.getHeight();

        // Resize image, if necessary.
        double sx = (double) maxWidth / x;
        double sy = (double) maxHeight / y;
        if (sx < 1 || sy < 1) {
            AffineTransform tx = new AffineTransform();
            if (sx < sy) tx.scale(sx, sx);
            else tx.scale(sy, sy);
            AffineTransformOp op = new AffineTransformOp(tx,
                    AffineTransformOp.TYPE_BILINEAR);
            image = op.filter(image, null);
        }

        // Pack and return the result.
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        return new ImageData(
                image.getWidth(),
                image.getHeight(),
                os.toByteArray());
    }
}
