package com.pathlopedia.servlet.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: Volkan YAZICI <volkan.yazici@gmail.com>
 * Date: 9/21/11
 * Time: 11:50 AM
 */
public abstract class PostMethodServlet extends GenericServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        wrap(req, res);
    }
}
