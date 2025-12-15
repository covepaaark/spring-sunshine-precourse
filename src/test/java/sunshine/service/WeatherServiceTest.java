package sunshine.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import sunshine.dto.CurrentWeather;
import sunshine.dto.WeatherResponse;
import sunshine.dto.WeatherSummary;
import sunshine.model.City;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void getWeather_shouldReturnWeatherSummary() {
        // Given
        City seoul = City.SEOUL;
        WeatherResponse mockResponse = new WeatherResponse();
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTemperature(15.0);
        currentWeather.setApparentTemperature(14.0);
        currentWeather.setHumidity(60);
        currentWeather.setWeatherCode(3);
        mockResponse.setCurrent(currentWeather);

        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class)))
                .thenReturn(mockResponse);

        // When
        WeatherSummary summary = weatherService.getWeather(seoul);

        // Then
        assertThat(summary).isNotNull();
        assertThat(summary.getSummary()).isEqualTo(
                "Currently in Seoul: 15.0°C (feels like 14.0°C), Humidity: 60%. Sky: Overcast."
        );
    }
}

