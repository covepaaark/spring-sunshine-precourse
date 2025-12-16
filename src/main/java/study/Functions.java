package study;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;

import java.time.LocalDate;
import java.util.function.Function;

@Configurable
public class Functions {
    @Description("Calculate a date after adding days from today")
    @Bean
    public Function<AddDayRequest, DateResponse> addDaysFromToday() {
        return request -> {
            LocalDate result = LocalDate.now().plusDays(request.days());
            return new DateResponse(result.toString());
        };
    }
}