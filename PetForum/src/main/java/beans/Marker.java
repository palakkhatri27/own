package beans;

public class Marker {
    private int id;
    private double lat;
    private double lng;
    private String animalName;

    public Marker() {}

    public Marker(int id, double lat, double lng, String animalName) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.animalName = animalName;
    }

    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for animal name (tag)
    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }
    
    // Getter for latitude
    public double getLat() {
        return lat;
    }

    // Setter for latitude
    public void setLat(double lat) {
        this.lat = lat;
    }

    // Getter for longitude
    public double getLng() {
        return lng;
    }

    // Setter for longitude
    public void setLng(double lng) {
        this.lng = lng;
    }
}
