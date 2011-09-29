package com.pathlopedia.servlet.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class PostMethodServlet extends GenericServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        wrap(req, res);
    }
}
