package com.example.scholarmatch.ai.service;
import com.example.scholarmatch.scholarship.dto.ScholarshipRequest;
import com.example.scholarmatch.scholarship.model.Scholarship;
import com.example.scholarmatch.student.model.Student;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Service
public class AIService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${ai.anthropic.api-key:}")
    private String apiKey;

    @Value("${ai.anthropic.model:claude-haiku-4-5-20251001}")
    private String model;

    @Value("${ai.anthropic.base-url:https://api.anthropic.com/v1/messages}")
    private String baseUrl;

    public AIService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public ScholarshipRequest extractScholarshipDetails(String rawDescription) {
        String systemPrompt = "You extract structured scholarship data from raw text. " +
                "Respond ONLY with a single JSON object, no preamble, no markdown fences. " +
                "JSON keys: title (string), description (string), sourceType (one of GOVERNMENT, " +
                "PRIVATE_CORPORATE, PRIVATE_NGO, INSTITUTION), fundingBodyName (string), amount (number or null), " +
                "applicationMode (EXTERNAL_REDIRECT or INSTITUTION_MANAGED), officialApplicationLink (string or null), " +
                "eligibleCategories (array of strings, subset of GENERAL/OBC/SC/ST/EWS/MINORITY, empty if open to all), " +
                "minAnnualIncome (number or null), maxAnnualIncome (number or null), " +
                "eligibleCourses (array of strings, empty if open to all), " +
                "educationLevel (one of SCHOOL, DIPLOMA, UNDERGRADUATE, POSTGRADUATE, PHD, ANY), " +
                "minMarksCgpa (number or null), eligibleStates (array of strings, empty if open to all), " +
                "eligibleSpecialStatus (array of strings, empty if none), deadline (date string in YYYY-MM-DD format). " +
                "If a field cannot be determined, use null or an empty array as appropriate.";

        String responseText = callClaude(systemPrompt, rawDescription);

        try {
            String json = stripCodeFences(responseText);
            return objectMapper.readValue(json, ScholarshipRequest.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse scholarship details from AI response: " + e.getMessage(), e);
        }
    }

    public String generateEligibilityExplanation(Student student,
                                                 Scholarship scholarship,
                                                 List<String> matched,
                                                 List<String> unmatched,
                                                 double matchPercentage,
                                                 String fallbackText) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackText;
        }

        try {
            String systemPrompt = "You write a short, plain-language eligibility explanation for a student, " +
                    "using ONLY the matched/unmatched criteria and match score given to you — never invent or " +
                    "guess eligibility facts. Structure your reply as exactly two labeled sentences: " +
                    "start the first with 'Why eligible: ' summarizing the criteria the student meets (or say " +
                    "'Why eligible: You currently meet none of the criteria.' if none matched), and start the " +
                    "second with 'Why not eligible: ' summarizing the criteria the student does not meet (or say " +
                    "'Why not eligible: You meet all eligibility criteria for this scholarship.' if none are " +
                    "unmatched). No markdown, no extra commentary";

            String userPrompt = String.format(
                    "Scholarship: %s%nMatch score: %.1f%%%nCriteria met: %s%nCriteria not met: %s",
                    scholarship.getTitle(),
                    matchPercentage,
                    matched.isEmpty() ? "none" : String.join(", ", matched),
                    unmatched.isEmpty() ? "none" : String.join(", ", unmatched)
            );

            String result = callClaude(systemPrompt, userPrompt);
            return (result == null || result.isBlank()) ? fallbackText : result.trim();
        } catch (Exception e) {
            return fallbackText;
        }
    }

    private String callClaude(String systemPrompt, String userMessage) {
        try {
            ObjectNode message = objectMapper.createObjectNode();
            message.put("role", "user");
            message.put("content", userMessage);

            ArrayNode messages = objectMapper.createArrayNode();
            messages.add(message);

            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", model);
            body.put("max_tokens", 1024);
            body.put("system", systemPrompt);
            body.set("messages", messages);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Anthropic API returned status " + response.statusCode() + ": " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode contentArray = root.path("content");
            StringBuilder text = new StringBuilder();
            if (contentArray.isArray()) {
                for (JsonNode block : contentArray) {
                    if ("text".equals(block.path("type").asText())) {
                        text.append(block.path("text").asText());
                    }
                }
            }
            return text.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Anthropic API call failed: " + e.getMessage(), e);
        }
    }

    private String stripCodeFences(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```[a-zA-Z]*", "");
            if (trimmed.endsWith("```")) {
                trimmed = trimmed.substring(0, trimmed.length() - 3);
            }
        }
        return trimmed.trim();
    }
}