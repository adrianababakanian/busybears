package cs160.dinestination;

/**
 * Created by adrianababakanian on 4/25/18.
 */

public class Marker {

    private String name;
    private String address;
    private Double lat;
    private Double lon;

    Marker(String name, String address, Double lat, Double lon) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    // Add the marker to the
    protected void addToMap() {

    }

    protected String getName() {
        return this.name;
    }

    protected Double getLat() {
        return this.lat;
    }

    protected Double getLon() {
        return this.lon;
    }

}
