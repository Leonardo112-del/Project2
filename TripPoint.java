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
                    int timeVal = Integer.parseInt(parts[0].trim());
                    double latVal = Double.parseDouble(parts[1].trim());
                    double lonVal = Double.parseDouble(parts[2].trim());
                    trip.add(new TripPoint(timeVal, latVal, lonVal));
                }
            }
        }
    }
}
