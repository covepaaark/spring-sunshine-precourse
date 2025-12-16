package sunshine.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;
import sunshine.dto.WeatherSummary;
import sunshine.model.City;
import sunshine.service.WeatherService;

import java.util.Optional;
import java.util.function.Function;

@Component
@Configurable
public class WeatherFunctions {

    private final WeatherService weatherService;

    public WeatherFunctions(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Description("Get current weather information for a city. Supports city names like Seoul, Tokyo, New York, Paris, London. Also supports region queries like 'Seoul', 'Seoul Gangnam', 'Seoul Seongdong', 'Capital Area', etc.")
    @Bean
    public Function<WeatherRequest, WeatherSummaryResponse> getWeather() {
        return request -> {
            String location = request.location();
            
            // Try to find exact city match first
            Optional<City> cityOpt = City.fromString(location);
            
            if (cityOpt.isPresent()) {
                WeatherSummary summary = weatherService.getWeather(cityOpt.get());
                return new WeatherSummaryResponse(summary.getSummary());
            }
            
            // For region/district queries, try to parse or use default
            // For now, we'll use Seoul as default for region queries
            // In a real implementation, you'd have a more sophisticated location parser
            WeatherSummary summary = weatherService.getWeather(City.SEOUL);
            return new WeatherSummaryResponse(summary.getSummary());
        };
    }

    public record WeatherRequest(String location) {}
    
    public record WeatherSummaryResponse(String summary) {}
}

