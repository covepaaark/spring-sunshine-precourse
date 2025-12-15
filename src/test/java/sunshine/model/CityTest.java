package sunshine.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CityTest {

    @ParameterizedTest
    @ValueSource(strings = {"seoul", "SEOUL", "Seoul"})
    void fromString_shouldReturnCityForValidName(String cityName) {
        // When
        Optional<City> result = City.fromString(cityName);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(City.SEOUL);
    }

    @Test
    void fromString_shouldReturnEmptyForInvalidName() {
        // When
        Optional<City> result = City.fromString("UnknownCity");

        // Then
        assertThat(result).isNotPresent();
    }
}

