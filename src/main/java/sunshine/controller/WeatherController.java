package sunshine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sunshine.dto.WeatherSummary;
import sunshine.model.City;
import sunshine.service.WeatherService;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherSummary> getWeather(@RequestParam String city) {
        return City.fromString(city)
                .map(weatherService::getWeather)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

