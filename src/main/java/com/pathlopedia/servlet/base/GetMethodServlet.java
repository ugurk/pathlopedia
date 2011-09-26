package com.pathlopedia.servlet.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: Volkan YAZICI <volkan.yazici@gmail.com>
 * Date: 9/21/11
 * Time: 11:47 AM
 */
public abstract class GetMethodServlet extends GenericServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        wrap(req, res);
    }
}
