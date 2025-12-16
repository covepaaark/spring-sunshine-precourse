package sunshine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sunshine.dto.WeatherWithRecommendation;

@Service
public class WeatherLLMService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherLLMService.class);
    
    private final LLMService llmService;
    
    @Value("${spring.ai.google.genai.chat.options.model:gemini-2.5-flash-lite}")
    private String modelName;

    public WeatherLLMService(LLMService llmService) {
        this.llmService = llmService;
    }

    public WeatherWithRecommendation getWeatherWithLLM(String location, boolean useCache) {
        if (useCache) {
            logger.info("Attempting to use cached data for location: {}", location);
        } else {
            logger.info("Cache disabled - fetching fresh data for location: {}", location);
        }

        // First part: Fetch weather information (cached)
        ChatResponse weatherInfoResponse = llmService.fetchWeatherInfo(location, useCache);
        String weatherInfo = weatherInfoResponse.getResult().getOutput().getText();
        
        logger.info("Weather information retrieved for location: {}", location);

        // Second part: Generate summary based on weather information (cached)
        ChatResponse summaryResponse = llmService.generateSummary(weatherInfo, location, useCache);
        String content = summaryResponse.getResult().getOutput().getText();
        
        // Parse the response to extract summary and recommendation
        String[] parts = content.split("RECOMMENDATION:");
        String summary = parts.length > 0 ? parts[0].replace("SUMMARY:", "").trim() : content;
        String recommendation = parts.length > 1 ? parts[1].trim() : "No specific recommendation provided.";

        // Extract usage information from metadata (combine both responses)
        int inputTokens = 0;
        int outputTokens = 0;
        int totalTokens = 0;
        
        // Add tokens from weather info response
        if (weatherInfoResponse.getMetadata() != null && weatherInfoResponse.getMetadata().getUsage() != null) {
            var usage = weatherInfoResponse.getMetadata().getUsage();
            inputTokens += usage.getPromptTokens();
            outputTokens += usage.getCompletionTokens();
            totalTokens += usage.getTotalTokens();
        }
        
        // Add tokens from summary response
        if (summaryResponse.getMetadata() != null && summaryResponse.getMetadata().getUsage() != null) {
            var usage = summaryResponse.getMetadata().getUsage();
            inputTokens += usage.getPromptTokens();
            outputTokens += usage.getCompletionTokens();
            totalTokens += usage.getTotalTokens();
        }
        
        // Estimate cost (Gemini 2.5 Flash Lite pricing - approximate)
        // Input: $1.25 per 1M tokens, Output: $10.00 per 1M tokens
        double estimatedCost = (inputTokens / 1_000_000.0 * 1.25) + (outputTokens / 1_000_000.0 * 10);

        // Log usage and cost
        logger.info("LLM Usage - Location: {}, Model: {}, Input Tokens: {}, Output Tokens: {}, Total Tokens: {}, Cache Enabled: {}, Estimated Cost: ${}", 
                location, modelName, inputTokens, outputTokens, totalTokens, useCache, String.format("%.6f", estimatedCost));

        return new WeatherWithRecommendation(summary, recommendation);
    }
}