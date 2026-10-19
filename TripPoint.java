import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TripPoint {
    private int time;
    private double lat;
    private double lon;
    private static ArrayList<TripPoint> trip = new ArrayList<>();
    private static final double EARTH_RADIUS = 6371.0;

    /**
     * Constructs a TripPoint with specified time, latitude, and longitude.
     */
    public TripPoint(int time, double lat, double lon) {
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Returns the elapsed time of this point.
     */
    public int getTime() {
        return this.time;
    }

    /**
     * Returns the latitude of this point.
     */
    public double getLat() {
        return this.lat;
    }

    /**
     * Returns the longitude of this point.
     */
    public double getLon() {
        return this.lon;
    }

    /**
     * Returns a safe shallow copy of the central trip tracking records.
     */
    public static ArrayList<TripPoint> getTrip() {
        return new ArrayList<>(trip);
    }
}
