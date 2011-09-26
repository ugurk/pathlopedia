package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.User;
import com.pathlopedia.servlet.entity.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdResponse;
import com.pathlopedia.servlet.entity.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * User: Volkan YAZICI <volkan.yazici@gmail.com>
 * Date: 9/21/11
 * Time: 11:11 AM
 */
public final class NewUserServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        // Create the user.
        User user = new User(
                User.parseType(req.getParameter("type")),
                req.getParameter("name"),
                req.getParameter("email"));

        // Check if there exists any user with this e-mail.
        Datastore ds = DatastorePortal.getDatastore();
        if (ds.find(User.class).filter("email", user.getEmail()).get() != null)
            throw new ServletException("E-mail address is in use!");

        // Save the user.
        DatastorePortal.safeSave(user);

        // Return success with the created user id.
        return new JSONResponse(0, new ObjectIdResponse(user.getId()));
    }
}
