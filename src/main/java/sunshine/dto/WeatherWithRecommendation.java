package sunshine.dto;

public class WeatherWithRecommendation {
    private final String weatherSummary;
    private final String clothingRecommendation;

    public WeatherWithRecommendation(String weatherSummary, String clothingRecommendation) {
        this.weatherSummary = weatherSummary;
        this.clothingRecommendation = clothingRecommendation;
    }

    public String getWeatherSummary() {
        return weatherSummary;
    }

    public String getClothingRecommendation() {
        return clothingRecommendation;
    }
}

