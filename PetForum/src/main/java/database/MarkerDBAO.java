package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import listener.ContextListener;
import beans.*;

public class MarkerDBAO {
    private Connection con;

    // Initialize the database connection
    public MarkerDBAO(HashMap<String,String> dbConfig) throws Exception {
        try {
        	String url = dbConfig.get("url");
        	String username = dbConfig.get("username");
        	String password = dbConfig.get("password");
            con = DriverManager.getConnection(url, username, password);
            
        } catch (Exception ex) {
            System.out.println("Exception in MarkerDBAO: " + ex);
            throw new Exception("Couldn't open connection to database: " +
                    ex.getMessage());
        }
    }

    public void remove() {
        try {
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    // Add a marker to the database
    public void addMarker(double lat, double lng, String animalName) throws SQLException {
        String query = "INSERT INTO markers (lat, lng, animal_name) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setDouble(1, lat);
        preparedStatement.setDouble(2, lng);
        preparedStatement.setString(3, animalName);
        preparedStatement.executeUpdate();
    }

    // Retrieve all markers from the database
    public List<Marker> getMarkers() throws SQLException {
        List<Marker> markers = new ArrayList<>();
        String query = "SELECT * FROM markers";
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            double lat = resultSet.getDouble("lat");
            double lng = resultSet.getDouble("lng");
            String animalName = resultSet.getString("animal_name");
            markers.add(new Marker(id, lat, lng, animalName));
        }

        return markers;
    }

    // Delete a marker from the database based on latitude and longitude
    public void deleteMarker(double lat, double lng) throws SQLException {
        String query = "DELETE FROM markers WHERE lat = ? AND lng = ?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setDouble(1, lat);
        preparedStatement.setDouble(2, lng);
        preparedStatement.executeUpdate();
    }
}
