package servlets;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import com.google.gson.Gson;
import database.MarkerDBAO;
import beans.Marker;

public class DeleteMarkerServlet extends HttpServlet {
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

        // Convert JSON to Marker object
        Gson gson = new Gson();
        Marker marker = gson.fromJson(jsonData.toString(), Marker.class);

        try {
            // Delete marker using DAO
            markerDBAO.deleteMarker(marker.getLat(), marker.getLng());
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\": \"success\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
