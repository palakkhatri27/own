package servlets;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import database.*;
import beans.*;

public class GetMarkersServlet extends HttpServlet {
    private MarkerDBAO markerDBAO;

    @Override
    public void init() throws ServletException {
        markerDBAO = (MarkerDBAO) getServletContext().getAttribute("markerDB");
        if (markerDBAO == null) {
            throw new UnavailableException("Couldn't get database.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Marker> markers = markerDBAO.getMarkers();
            Gson gson = new Gson();
            String jsonMarkers = gson.toJson(markers);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonMarkers);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database error occurred.\"}");
        }
    }
}
