package sunshine.dto;

public class WeatherSummary {

    private final String summary;

    public WeatherSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }
}

