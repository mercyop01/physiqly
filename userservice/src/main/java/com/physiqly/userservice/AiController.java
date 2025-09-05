package com.physiqly.userservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AiController {

    private final WebClient webClient;

    // We'll inject the Google API URL from our properties file later.
    private final String googleApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-05-20:generateContent?key=";
    
    // In a real app, you would load this key securely from environment variables.
    // For this environment, the key will be provided automatically.
    private final String apiKey = ""; 

    public AiController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(googleApiUrl + apiKey).build();
    }

    @PostMapping("/chat")
    public Mono<String> chatWithAi(@RequestBody String userPrompt) {
        
        String systemPrompt = "You are Physiqly, a friendly, encouraging, and knowledgeable AI fitness coach. Provide helpful and safe advice on workouts, nutrition, and general fitness. Keep your answers concise and easy to understand. Do not give medical advice.";

        // Construct the payload for the Google API
        Object payload = new Object() {
            public final Object[] contents = {
                new Object() {
                    public final Object[] parts = {
                        new Object() {
                            public final String text = userPrompt;
                        }
                    };
                }
            };
            public final Object systemInstruction = new Object() {
                 public final Object[] parts = {
                        new Object() {
                            public final String text = systemPrompt;
                        }
                    };
            };
        };

        // Make the server-to-server call to the Gemini API
        return this.webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class) // We get the raw JSON response
                .map(this::extractTextFromResponse); // We extract just the text part
    }

    // A helper method to parse the complex JSON response from Google
    // and return only the text we want to show the user.
    private String extractTextFromResponse(String jsonResponse) {
        try {
            // This is a simplified parser. A real app would use a library like Jackson.
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(jsonResponse);
            String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I had trouble understanding the response.";
        }
    }
}
