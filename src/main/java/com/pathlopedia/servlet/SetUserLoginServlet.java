package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.User;
import com.pathlopedia.servlet.entity.JSONResponse;
import com.pathlopedia.servlet.entity.SetUserLoginResponse;
import com.pathlopedia.servlet.entity.WritableResponse;
import com.pathlopedia.servlet.wrapper.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * User: Volkan YAZICI <volkan.yazici@gmail.com>
 * Date: 9/21/11
 * Time: 11:40 AM
 */
public final class SetUserLoginServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest httpReq)
            throws IOException, ServletException {
        // TODO Requires querying!
        HttpSession ses = httpReq.getSession();
        Datastore ds = DatastorePortal.getDatastore();

        // Fetch user.
        User user = DatastorePortal.safeGet(
                User.class, httpReq.getParameter("id"));

        // Set appropriate session variables.
        ses.setAttribute("user", user);
        ses.setAttribute("userId", user.getId().toString());
        ses.setAttribute("userKey", new Key<User>(User.class, user.getId()));

        // Pack and return the result.
        return new JSONResponse(0,
                new SetUserLoginResponse(
                        user.getId().toString(),
                        user.getName()));
    }
}
