package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Coordinate;
import com.pathlopedia.datastore.entity.Corner;
import com.pathlopedia.datastore.entity.Parent;
import com.pathlopedia.datastore.entity.Path;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public final class PathCornerSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Fetch given path.
        Path path = DatastorePortal.safeGet(
                Path.class, getTrimmedParameter("path"));

        // Check path visibility.
        if (!path.isVisible())
            throw new ServletException("Inactive path!");

        // Check path accessibility.
        if (!path.isEditable(getSessionUser()))
            throw new ServletException("Access denied!");

        // Parse input (which is, a JSON vector of Coordinates) corners.
        List<Corner> corners = new ArrayList<Corner>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Iterator<JsonNode> it = mapper.readValue(
                    getTrimmedParameter("corners"), ArrayNode.class)
                    .getElements();
            Parent parent = new Parent(path);
            while (it.hasNext())
                corners.add(new Corner(parent,
                        mapper.readValue(it.next(), Coordinate.class)));
        } catch (Exception e) {
            throw new ServletException(
                    "Invalid 'corners' parameter: "+e.getMessage());
        }

        // Deactivate existing corners.
        Datastore ds = DatastorePortal.getDatastore();
        DatastorePortal.safeUpdate(
                ds.find(Corner.class).filter("parent.path", path),
                ds.createUpdateOperations(Corner.class).set("visible", false));

        // Save new corners.
        DatastorePortal.safeSave(corners);

        // Update the path.
        DatastorePortal.safeUpdate(path,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Path.class)
                        .set("updatedAt", new Date())
                        .set("corners", corners));

        // Return success.
        return new JSONResponse(0);
    }
}
