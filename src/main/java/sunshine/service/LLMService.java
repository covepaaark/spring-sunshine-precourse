package sunshine.service;

import org.springframework.ai.chat.model.ChatResponse;

public interface LLMService {
    /**
     * First part: Fetch weather information using the getWeather function
     */
    ChatResponse fetchWeatherInfo(String location, boolean useCache);
    
    /**
     * Second part: Generate a summary and a recommendation based on weather information
     */
    ChatResponse generateSummary(String weatherInfo, String location, boolean useCache);
}

