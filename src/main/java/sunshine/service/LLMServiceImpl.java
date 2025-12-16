package sunshine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import sunshine.config.WeatherFunctions;

@Service
public class LLMServiceImpl implements LLMService {

    private static final Logger logger = LoggerFactory.getLogger(LLMServiceImpl.class);

    private final ChatClient chatClient;
    private final WeatherFunctions weatherFunctions;

    public LLMServiceImpl(ChatClient.Builder chatClientBuilder, WeatherFunctions weatherFunctions) {
        this.chatClient = chatClientBuilder.build();
        this.weatherFunctions = weatherFunctions;
    }

    @Override
    @Cacheable(value = "weatherInfo", key = "#location", condition = "#useCache == true")
    public ChatResponse fetchWeatherInfo(String location, boolean useCache) {
        logger.info("[fetchWeatherInfo] Starting - Location: {}, UseCache: {}", location, useCache);
        
        String systemPrompt = """
            You are a helpful weather assistant.
            When asked about weather, use the getWeather function to fetch current weather data.
            Return only the raw weather information without any summary or recommendation.
            """;

        String userPrompt = String.format("What's the weather like in %s? Use the getWeather function to fetch the current weather data.", location);

        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemPrompt);
        UserMessage userMessage = new UserMessage(userPrompt);
        Prompt prompt = new Prompt(userMessage, systemTemplate.createMessage());

        ChatResponse response = chatClient.prompt(prompt)
                .toolNames("getWeather")
                .call()
                .chatResponse();

        String responseText = response.getResult().getOutput().getText();
        logger.info("[fetchWeatherInfo] Response received for location: {}", location);
        logger.info("[fetchWeatherInfo] Response content: {}", responseText);
        
        // Log usage information if available
        if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
            var usage = response.getMetadata().getUsage();
            logger.info("[fetchWeatherInfo] Usage - Input Tokens: {}, Output Tokens: {}, Total Tokens: {}", 
                    usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        }

        return response;
    }

    @Override
    @Cacheable(value = "weatherSummaries", key = "#location + '_' + #weatherInfo.hashCode()", condition = "#useCache == true")
    public ChatResponse generateSummary(String weatherInfo, String location, boolean useCache) {
        logger.info("[generateSummary] Starting - Location: {}, UseCache: {}", location, useCache);
        logger.debug("[generateSummary] Weather info input: {}", weatherInfo);
        
        String systemPrompt = """
            You are a helpful weather assistant.
            Based on the provided weather information, provide a concise summary (2-3 sentences) and recommend appropriate clothing based on:
            - Temperature ranges (e.g., below 0°C: heavy winter clothes, 0-10°C: warm clothes, 10-20°C: light jacket, 20-30°C: light clothes, above 30°C: very light clothes)
            - Precipitation (rain/snow: bring umbrella or raincoat)
            - Apparent temperature (feels like temperature)
            - Wind conditions (if windy: consider windbreaker)
            
            Format your response as:
            SUMMARY: [weather summary]
            RECOMMENDATION: [clothing recommendation]
            """;

        String userPrompt = String.format("Based on the following weather information for %s, provide a summary and clothing recommendation:\n\n%s", location, weatherInfo);

        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemPrompt);
        UserMessage userMessage = new UserMessage(userPrompt);
        Prompt prompt = new Prompt(userMessage, systemTemplate.createMessage());

        ChatResponse response = chatClient.prompt(prompt)
                .call()
                .chatResponse();

        String responseText = response.getResult().getOutput().getText();
        logger.info("[generateSummary] Response received for location: {}", location);
        logger.info("[generateSummary] Response content: {}", responseText);
        
        // Log usage information if available
        if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
            var usage = response.getMetadata().getUsage();
            logger.info("[generateSummary] Usage - Input Tokens: {}, Output Tokens: {}, Total Tokens: {}", 
                    usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        }

        return response;
    }
}