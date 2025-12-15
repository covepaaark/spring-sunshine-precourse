package sunshine.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class WeatherCodeConverter {

    private static final Map<Predicate<Integer>, String> CODE_MAP = new LinkedHashMap<>();

    static {
        CODE_MAP.put(code -> code == 0, "Clear sky");
        CODE_MAP.put(code -> code == 1, "Mainly clear");
        CODE_MAP.put(code -> code == 2, "Partly cloudy");
        CODE_MAP.put(code -> code == 3, "Overcast");
        CODE_MAP.put(code -> code == 45, "Fog");
        CODE_MAP.put(code -> code == 48, "Depositing rime fog");
        CODE_MAP.put(code -> code >= 51 && code <= 55, "Drizzle");
        CODE_MAP.put(code -> code >= 61 && code <= 65, "Rain");
        CODE_MAP.put(code -> code >= 66 && code <= 67, "Freezing Rain");
        CODE_MAP.put(code -> code >= 71 && code <= 75, "Snow fall");
        CODE_MAP.put(code -> code == 77, "Snow grains");
        CODE_MAP.put(code -> code >= 80 && code <= 82, "Rain showers");
        CODE_MAP.put(code -> code >= 85 && code <= 86, "Snow showers");
        CODE_MAP.put(code -> code == 95, "Thunderstorm");
        CODE_MAP.put(code -> code >= 96 && code <= 99, "Thunderstorm with hail");
    }

    public static String toText(int code) {
        return CODE_MAP.entrySet().stream()
                .filter(entry -> entry.getKey().test(code))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("Unknown weather condition");
    }
}

