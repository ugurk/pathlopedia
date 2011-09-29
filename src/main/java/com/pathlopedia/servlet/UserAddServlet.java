package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.User;
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
 * Time: 11:11 AM
 */
public final class UserAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        // Create the user.
        User user = new User(
                User.parseType(req.getParameter("type")),
                req.getParameter("name"),
                req.getParameter("email"));

        // Check if there exists any user with this e-mail.
        Datastore ds = DatastorePortal.getDatastore();
        User existingUser = ds.find(User.class)
                .filter("visible", true)
                .filter("email", user.getEmail())
                .get();
        if (existingUser != null)
            throw new ServletException("E-mail address is in use!");

        // Save the user.
        DatastorePortal.safeSave(user);

        // Return success with the created user id.
        return new JSONResponse(0, new ObjectIdEntity(user.getId()));
    }
}
