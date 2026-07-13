package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key:AIzaSy_la_teva_clau_real_aqui}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    public String generateStudyPlanJson(String subject, LocalDate examDate, int totalHours) {
// Apuntem al model de nova generació que demana el teu compte a la v1beta
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;        // Hem reforçat el prompt perquè la IA entengui de veritat que NO volem markdown (```json ... ```)
        String prompt = String.format(
                "Actúa como un planificador de estudio estructurado. Un estudiante tiene un examen de %s el día %s " +
                        "y dispone de un total de %d horas de estudio. Devuélveme un calendario detallado estructurado en " +
                        "formato JSON estricto. El resultado debe ser únicamente un array de objetos con las siguientes llaves: " +
                        "'date' (en formato YYYY-MM-DD), 'durationMinutes' (número entero) y 'studyMethod' (una breve explicación " +
                        "técnica en inglés de la sesión, ej: Pomodoro, Active Recall o Mock exam). " +
                        "MUY IMPORTANTE: No agregues texto adicional fuera del JSON, no uses introducciones, y NO envuelvas el resultado en bloques de código markdown como ```json ... ```. Devuelve el JSON crudo empezando por [ y terminando por ].",
                subject, examDate.toString(), totalHours
        );

        // Eliminem completament el bloc "generation_config" que feia fallar la petició
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            Map<String, Object> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            String rawText = (String) parts.get(0).get("text");

            // Per si de cas Gemini es despista i et posa els blocs de markdown ```json, els netegem a mà per seguretat:
            return rawText.trim().replaceAll("^```json", "").replaceAll("^```", "").replaceAll("```$", "").trim();

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con la API de Gemini: " + e.getMessage(), e);
        }
    }
}