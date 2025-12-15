package sunshine.model;

import java.util.Arrays;
import java.util.Optional;

public enum City {
    SEOUL("Seoul", 37.5665, 126.9780),
    TOKYO("Tokyo", 35.6895, 139.6917),
    NEW_YORK("New York", 40.7128, -74.0060),
    PARIS("Paris", 48.8566, 2.3522),
    LONDON("London", 51.5074, -0.1278);

    private final String name;
    private final double latitude;
    private final double longitude;

    City(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static Optional<City> fromString(String text) {
        return Arrays.stream(values())
                .filter(city -> city.name.equalsIgnoreCase(text))
                .findFirst();
    }
}

