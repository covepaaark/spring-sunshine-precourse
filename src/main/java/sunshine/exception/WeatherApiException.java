package sunshine.exception;

public class WeatherApiException extends RuntimeException {

    public WeatherApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

