package com.pathlopedia.servlet;

import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.GetMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * User: Volkan YAZICI <volkan.yazici@gmail.com>
 * Date: 9/21/11
 * Time: 1:03 PM
 */
public final class SetUserLogoutServlet extends GetMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        req.getSession().invalidate();
        return new JSONResponse(0);
    }
}
