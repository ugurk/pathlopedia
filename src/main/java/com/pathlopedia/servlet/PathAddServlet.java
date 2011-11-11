package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Path;
import com.pathlopedia.datastore.entity.ShareType;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdEntity;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class PathAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Create a path instance.
        Path path = new Path(
                getSessionUser(),
                getTrimmedParameter("title"),
                getTrimmedParameter("text"),
                ShareType.parse(getTrimmedParameter("shareType")));

        // Save the created instance.
        DatastorePortal.safeSave(path);

        // Pack and return the path id.
        return new JSONResponse(0, new ObjectIdEntity(path.getId()));
    }
}
