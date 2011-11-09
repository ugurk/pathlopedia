package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.User;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdEntity;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class UserAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        // Create the user.
        User user = new User(
                User.parseType(getTrimmedParameter("type")),
                getTrimmedParameter("name"),
                getTrimmedParameter("email"));

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
