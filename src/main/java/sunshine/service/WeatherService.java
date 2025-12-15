package sunshine.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sunshine.dto.CurrentWeather;
import sunshine.dto.WeatherResponse;
import sunshine.dto.WeatherSummary;
import sunshine.model.City;
import sunshine.util.WeatherCodeConverter;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast";

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherSummary getWeather(City city) {
        WeatherResponse weatherResponse = fetchWeatherData(city);
        return createWeatherSummary(city, weatherResponse);
    }

    private WeatherResponse fetchWeatherData(City city) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("latitude", city.getLatitude())
                .queryParam("longitude", city.getLongitude())
                .queryParam("current", "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code")
                .queryParam("timezone", "auto")
                .toUriString();

        return restTemplate.getForObject(url, WeatherResponse.class);
    }

    private WeatherSummary createWeatherSummary(City city, WeatherResponse weatherResponse) {
        CurrentWeather currentWeather = weatherResponse.getCurrent();
        String description = WeatherCodeConverter.toText(currentWeather.getWeatherCode());

        String summary = String.format(
                "Currently in %s: %.1f°C (feels like %.1f°C), Humidity: %d%%. Sky: %s.",
                city.getName(),
                currentWeather.getTemperature(),
                currentWeather.getApparentTemperature(),
                currentWeather.getHumidity(),
                description
        );

        return new WeatherSummary(summary);
    }
}

