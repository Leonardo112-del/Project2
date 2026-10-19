import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents a single GPS tracking point within a triplog dataset.
 * Tracks elapsed time, latitude, and longitude coordinates.
 */
public class TripPoint {

    // Instance variables
    private int time; // in minutes
    private double lat;
    private double lon;

    // Static structures to hold cumulative trip points
    private static ArrayList<TripPoint> trip = new ArrayList<>();
    private static final double EARTH_RADIUS = 6371.0; // Radius of Earth in kilometers

    /**
     * Constructs a TripPoint with specified time, latitude, and longitude.
     * 
     * @param time elapsed time in minutes
     * @param lat  latitude coordinate
     * @param lon  longitude coordinate
     */
    public TripPoint(int time, double lat, double lon) {
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Returns the elapsed time of this point.
     * @return time in minutes
     */
    public int getTime() {
        return this.time;
    }

    /**
     * Returns the latitude of this point.
     * @return latitude value
     */
    public double getLat() {
        return this.lat;
    }

    /**
     * Returns the longitude of this point.
     * @return longitude value
     */
    public double getLon() {
        return this.lon;
    }

    /**
     * Returns a safe shallow copy of the central trip tracking records.
     * @return an ArrayList containing all tracked trip points
     */
    public static ArrayList<TripPoint> getTrip() {
        return new ArrayList<>(trip);
    }

    /**
     * Reads and parses structured data from a comma-separated values (CSV) log file.
     * Drops data into the active trip records structure.
     * 
     * @param filename the relative path to the target csv file
     * @throws FileNotFoundException if specified resource path cannot be resolved
     */
    public static void readFile(String filename) throws FileNotFoundException {
        // Clear previous state if reloading files
        trip.clear();
        
        File file = new File(filename);
        try (Scanner scanner = new Scanner(file)) {
            // Read and bypass the CSV header text row
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            
            // Loop through remaining entries line by line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Unpack comma-delimited columns: Time, Latitude, Longitude
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int timeVal = Integer.parseInt(parts[0].trim());
                    double latVal = Double.parseDouble(parts[1].trim());
                    double lonVal = Double.parseDouble(parts[2].trim());
                    
                    trip.add(new TripPoint(timeVal, latVal, lonVal));
                }
            }
        }
    }

    /**
     * Calculates the total elapsed trip time converted into hours.
     * @return total duration in hours
     */
    public static double totalTime() {
        if (trip.isEmpty()) {
            return 0.0;
        }
        // Total minutes is derived from the final point index marker entry
        int totalMinutes = trip.get(trip.size() - 1).getTime();
        return totalMinutes / 60.0;
    }

    /**
     * Computes the great-circle sphere distance between two tracking coordinates 
     * using the standard mathematical Haversine formulation model.
     * 
     * @param a starting point position
     * @param b terminal destination position
     * @return great-circle path length separation distance in kilometers
     */
    public static double haversineDistance(TripPoint a, TripPoint b) {
        double lat1 = Math.toRadians(a.getLat());
        double lon1 = Math.toRadians(a.getLon());
        double lat2 = Math.toRadians(b.getLat());
        double lon2 = Math.toRadians(b.getLon());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Apply Haversine trigonometric identities
        double sinDeltaLat = Math.sin(dLat / 2.0);
        double sinDeltaLon = Math.sin(dLon / 2.0);
        
        double term = (sinDeltaLat * sinDeltaLat) + 
                      Math.cos(lat1) * Math.cos(lat2) * (sinDeltaLon * sinDeltaLon);
                      
        double centralAngle = 2.0 * Math.asin(Math.sqrt(term));
        return EARTH_RADIUS * centralAngle;
    }

    /**
     * Accumulates step distances over every sequential point tracked throughout the trip array.
     * @return grand total trajectory length in kilometers
     */
    public static double totalDistance() {
        if (trip.size() < 2) {
            return 0.0;
        }
        
        double accumulatedDist = 0.0;
        for (int i = 0; i < trip.size() - 1; i++) {
            accumulatedDist += haversineDistance(trip.get(i), trip.get(i + 1));
        }
        return accumulatedDist;
    }

    /**
     * Determines average cross velocity speed metrics between any two selective checkpoints.
     * Operates uniformly regardless of structural argument input sequence ordering.
     * 
     * @param a first position checkpoint
     * @param b second position checkpoint
     * @return absolute velocity metric expressed in kilometers per hour
     */
    public static double avgSpeed(TripPoint a, TripPoint b) {
        double distance = haversineDistance(a, b);
        // Take absolute difference in timing intervals to handle arbitrary argument tracking safely
        double timeDiffMinutes = Math.abs(a.getTime() - b.getTime());
        
        if (timeDiffMinutes == 0) {
            return 0.0;
        }
        
        double timeDiffHours = timeDiffMinutes / 60.0;
        return distance / timeDiffHours;
    }
}
