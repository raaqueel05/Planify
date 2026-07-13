package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class studySessionController {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private GroqService groqService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/createPlan")
    public String createStudyPlan(@RequestBody examRequest request) {
        try {
            String aiResponseJson = groqService.generateStudyPlanJson(
                    request.getSubject(),
                    request.getExamDate(),
                    request.getTotalHours()
            );

            List<studySession> generatedSessions = objectMapper.readValue(
                    aiResponseJson,
                    new TypeReference<List<studySession>>() {}
            );

            sessionRepository.saveAll(generatedSessions);

            return "Successfully created a customized AI study plan with " + generatedSessions.size() + " sessions";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating the AI study plan: "  + e.getMessage();
        }
    }
}
