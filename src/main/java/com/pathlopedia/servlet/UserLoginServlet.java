package com.pathlopedia.servlet;

import com.google.code.morphia.Key;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.User;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.UserLoginEntity;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public final class UserLoginServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        // Fetch user.
        User user = DatastorePortal.safeGet(
                User.class, getTrimmedParameter("id"));

        // Check user visibility.
        if (!user.isVisible())
            throw new ServletException("Inactive user!");

        // Set appropriate session variables.
        HttpSession ses = req.getSession();
        ses.setAttribute("user", user);
        ses.setAttribute("userId", user.getId().toString());
        ses.setAttribute("userKey", new Key<User>(User.class, user.getId()));

        // Pack and return the result.
        return new JSONResponse(0,
                new UserLoginEntity(
                        user.getId().toString(),
                        user.getName()));
    }
}
