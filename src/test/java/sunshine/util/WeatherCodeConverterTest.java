package sunshine.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherCodeConverterTest {

    @ParameterizedTest
    @CsvSource({
            "0, Clear sky",
            "3, Overcast",
            "51, Drizzle",
            "61, Rain",
            "71, Snow fall",
            "95, Thunderstorm",
            "99, Thunderstorm with hail",
            "100, Unknown weather condition"
    })
    void toText_shouldReturnCorrectDescription(int code, String expectedDescription) {
        // When
        String description = WeatherCodeConverter.toText(code);

        // Then
        assertThat(description).isEqualTo(expectedDescription);
    }
}

