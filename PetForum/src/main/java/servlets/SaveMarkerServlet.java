package servlets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import database.*;
import beans.*;

public class SaveMarkerServlet extends HttpServlet {
    private MarkerDBAO markerDBAO;

    @Override
    public void init() throws ServletException {
        markerDBAO = (MarkerDBAO) getServletContext().getAttribute("markerDB");
        if (markerDBAO == null) {
            throw new UnavailableException("Couldn't get database.");
        }
    }

    @Override
    public void destroy() {
        markerDBAO = null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Parse request data
        BufferedReader reader = request.getReader();
        StringBuilder jsonData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonData.append(line);
        }

        if (jsonData.length() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Empty request body.\"}");
            return;
        }
        
        // Convert JSON to Marker object
        Gson gson = new Gson();
        Marker marker;
        try {
            marker = gson.fromJson(jsonData.toString(), Marker.class);
        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid JSON format.\"}");
            return;
        }
        
        try {
            // Save marker using DAO
            markerDBAO.addMarker(marker.getLat(), marker.getLng(), marker.getAnimalName());
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\": \"success\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to save marker.\"}");
        }
    }
}
