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

    /**
     * Reads and parses structured data from a comma-separated values (CSV) log file.
     */
    public static void readFile(String filename) throws FileNotFoundException {
        trip.clear();
        File file = new File(filename);
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int timeVal = Integer.parseInt(parts.trim());
                    double latVal = Double.parseDouble(parts.trim());
                    double lonVal = Double.parseDouble(parts.trim());
                    trip.add(new TripPoint(timeVal, latVal, lonVal));
                }
            }
        }
    }

    /**
     * Calculates the total elapsed trip time converted into hours.
     */
    public static double totalTime() {
        if (trip.isEmpty()) {
            return 0.0;
        }
        int totalMinutes = trip.get(trip.size() - 1).getTime();
        return totalMinutes / 60.0;
    }

    /**
     * Computes the great-circle sphere distance between two tracking coordinates 
     * using the standard mathematical Haversine formulation model.
     */
    public static double haversineDistance(TripPoint a, TripPoint b) {
        double lat1 = Math.toRadians(a.getLat());
        double lon1 = Math.toRadians(a.getLon());
        double lat2 = Math.toRadians(b.getLat());
        double lon2 = Math.toRadians(b.getLon());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double sinDeltaLat = Math.sin(dLat / 2.0);
        double sinDeltaLon = Math.sin(dLon / 2.0);
        
        double term = (sinDeltaLat * sinDeltaLat) + 
                      Math.cos(lat1) * Math.cos(lat2) * (sinDeltaLon * sinDeltaLon);
                      
        double centralAngle = 2.0 * Math.asin(Math.sqrt(term));
        return EARTH_RADIUS * centralAngle;
    }
}
