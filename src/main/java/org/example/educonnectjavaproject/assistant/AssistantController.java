package org.example.educonnectjavaproject.assistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AssistantController {
    private String apiKey = "AIzaSyCDBvBTXTjM7vnhASslwyCQZBZ_LzB3Do4";
    private List<AiDialog> dialogList = new ArrayList<>();

    @PostMapping("/assistant")
    public String assistant(Model model) {
        String welcomeMessage = "Ողջույն, ես EduConnect-ի օգնականն եմ: Ինչով կարող եմ օգնել Ձեզ այս կրթական հարթակում: Խնդրում եմ նշեք Ձեր հարցը կամ խնդիրը:";
        model.addAttribute("answer", welcomeMessage);
        return "assistant";
    }

    @PostMapping("/assistant/ask")
    public ResponseEntity<String> askAssistant(@RequestBody Map<String, String> requestBody) {
        String currentQuestion = requestBody.getOrDefault("question", "Խնդրում եմ օգնել ինձ:");

        StringBuilder history = new StringBuilder();
        for (AiDialog dialog : dialogList) {
            history.append("User: ").append(dialog.getQuestion()).append("\n");
            history.append("Assistant: ").append(dialog.getAnswer()).append("\n");
        }
        // Ավելացնում ենք ընթացիկ հարցը պատմությանը
        String fullPrompt = history + "User: " + currentQuestion;
        System.out.println("Full prompt sent to API: " + fullPrompt);

        try {
            HttpClient client = HttpClient.newHttpClient();
            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/tunedModels/educonnectextendedquestions-ijs31d9oog1r:generateContent?key=" + apiKey;

            String requestBodyJson = """
            {
              "contents": [{
                "parts": [{
                  "text": "%s"
                }]
              }],
              "generationConfig": {
                "maxOutputTokens": 500,
                "temperature": 0.7,
                "topP": 0.9
              }
            }
            """.formatted(fullPrompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Raw API response: " + response.body());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());
                String answer = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

                // Հեռացնում ենք fullPrompt-ը պատասխանից, եթե այն կրկնվում է
                if (answer.startsWith(fullPrompt)) {
                    answer = answer.substring(fullPrompt.length()).trim();
                }
                if (answer.isEmpty() || answer.length() < 3) {
                    answer = "Կներեք, չկարողացա պատասխանել: Խնդրում եմ հստակեցնել հարցը:";
                }
                dialogList.add(new AiDialog(currentQuestion, answer));
                return ResponseEntity.ok(answer);
            } else {
                return ResponseEntity.status(response.statusCode()).body("API error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Կապի խնդիր։ " + e.getMessage());
        }
    }
}