package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.User;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class UserNameSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Validate input user name.
        String name = getTrimmedParameter("name");
        User.validateName(name);

        // Get user.
        @SuppressWarnings("unchecked")
        User user = (User) req.getSession().getAttribute("user");

        // Update the user.
        DatastorePortal.safeUpdate(user,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(User.class)
                        .set("name", name)
                        .set("updatedAt", new Date()));

        // Return success.
        return new JSONResponse(0);
    }
}
