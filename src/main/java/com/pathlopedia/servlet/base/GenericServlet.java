package com.pathlopedia.servlet.base;

import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.util.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

abstract class GenericServlet extends HttpServlet {
    void wrap(HttpServletRequest req, HttpServletResponse res) {
        try {
            process(req).write(res);
        } catch (Exception error) {
            Logger.l.error("Couldn't process HTTP request!", error);
            try {
                // TODO Some exceptions don't have messages, .e.g., NPE.
                // We should somehow log their stack traces.
                (new JSONResponse(1, error.getMessage())).write(res);
            } catch (Exception fatal) {
                Logger.l.fatal("Couldn't inform client!", fatal);
            }
        }
    }

    abstract protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException;

    protected void requireLogin(HttpServletRequest req)
            throws ServletException {
        HttpSession ses = req.getSession();
        if (ses.getAttribute("user") == null)
            throw new ServletException("Login required for this operation! "+req.getSession().getAttribute("userId"));
    }
}
