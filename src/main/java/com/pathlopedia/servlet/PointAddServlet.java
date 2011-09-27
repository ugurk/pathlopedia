package com.pathlopedia.servlet;

import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.*;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdEntity;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * User: Volkan YAZICI <volkan.yazici@gmail.com>
 * Date: 9/21/11
 * Time: 4:58 PM
 */
public final class PointAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Create the point.
        Point p = new Point(
                new Coordinate(
                        Double.parseDouble(req.getParameter("lat")),
                        Double.parseDouble(req.getParameter("lng"))),
                (User) req.getSession().getAttribute("user"),
                req.getParameter("title"),
                req.getParameter("text"));

        // Save the point.
        DatastorePortal.safeSave(p);

        // Pack and return the result.
        return new JSONResponse(0, new ObjectIdEntity(p.getId()));
    }
}
