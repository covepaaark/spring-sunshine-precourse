package sunshine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sunshine.dto.WeatherSummary;
import sunshine.dto.WeatherWithRecommendation;
import sunshine.exception.InvalidCityException;
import sunshine.model.City;
import sunshine.service.WeatherService;
import sunshine.service.WeatherLLMService;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherLLMService weatherLLMService;

    public WeatherController(WeatherService weatherService, WeatherLLMService weatherLLMService) {
        this.weatherService = weatherService;
        this.weatherLLMService = weatherLLMService;
    }

    @GetMapping
    public ResponseEntity<WeatherSummary> getWeather(@RequestParam String city) {
        City cityEnum = validateCity(city);
        WeatherSummary summary = weatherService.getWeather(cityEnum);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/llm")
    public ResponseEntity<WeatherWithRecommendation> getWeatherWithLLM(
            @RequestParam String location,
            @RequestParam(defaultValue = "false") boolean useCache) {
        WeatherWithRecommendation result = weatherLLMService.getWeatherWithLLM(location, useCache);
        return ResponseEntity.ok(result);
    }

    private City validateCity(String cityName) {
        return City.fromString(cityName)
                .orElseThrow(() -> new InvalidCityException("Invalid city name: " + cityName));
    }
}


