package com.pathlopedia.servlet.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class GetMethodServlet extends GenericServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        wrap(req, res);
    }
}
