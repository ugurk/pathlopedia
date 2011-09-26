package com.pathlopedia.servlet;

import com.pathlopedia.servlet.entity.JSONResponse;
import com.pathlopedia.servlet.entity.WritableResponse;
import com.pathlopedia.servlet.wrapper.GetMethodServlet;

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
