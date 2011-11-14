package com.pathlopedia.servlet;

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
                getTrimmedParameter("name"),
                getTrimmedParameter("email"));

        // Check if there exists any user with this e-mail.
        User existingUser = DatastorePortal.getDatastore()
                .find(User.class)
                .filter("visible", true)
                .filter("email", user.getEmail())
                .get();
        if (existingUser != null)
            throw new ServletException("E-mail address is already in use!");

        // Save the user.
        DatastorePortal.safeSave(user);

        // Return success with the created user id.
        return new JSONResponse(0, new ObjectIdEntity(user.getId()));
    }
}
