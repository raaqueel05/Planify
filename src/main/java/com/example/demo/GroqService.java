package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    public String generateStudyPlanJson(String subject, LocalDate examDate, int totalHours) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        String prompt = String.format(
                "Actúa como un planificador de estudio estructurado. Un estudiante tiene un examen de %s el día %s " +
                        "y dispone de un total de %d horas de estudio. Devuélveme un calendario detallado estructurado en " +
                        "formato JSON estricto. El resultado debe ser únicamente un array de objetos con las siguientes llaves: " +
                        "'date' (en formato YYYY-MM-DD), 'duration' (número entero de minutos) y 'studyMethod' (una breve explicación " +
                        "técnica en inglés de la sesión, ej: Pomodoro, Active Recall o Mock exam). " +
                        "CRÍTICO: Todas las fechas generadas deben ser estrictamente ANTERIORES a la fecha del examen (%s). No generes ninguna sesión para el mismo día del examen ni para días posteriores. " +
                        "No agregues texto adicional fuera del JSON ni uses bloques de código markdown.",
                subject, examDate.toString(), totalHours, examDate.toString() // <--- Passem la data dues vegades
        );

        // FIX: Aquesta és l'estructura ESTÀNDARD que entén Groq (model, messages, temperature)
        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "system", "content", "Eres un experto en pedagogía que habla únicamente en formato JSON estricto."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );

        try {
            String bearerToken = "Bearer " + apiKey.trim();

            Map<String, Object> response = restClient.post()
                    .uri(url)
                    .header("Authorization", bearerToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String rawText = (String) message.get("content");

            return rawText.trim().replaceAll("^```json", "").replaceAll("^```", "").replaceAll("```$", "").trim();

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con la API de Groq: " + e.getMessage(), e);
        }
    }
}