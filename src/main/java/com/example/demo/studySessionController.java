package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sessions")
public class studySessionController {
    @Autowired
    private SessionRepository sessionRepository;

    @GetMapping("/createTest")
    public String createTest(){
        studySession studySession = new studySession(
                java.time.LocalDate.now(),
                90,
                "Pomodoro"
        );
        sessionRepository.save(studySession);

        return "success";
    }

    @GetMapping("/list")
    public java.util.List<studySession> list(){
        return sessionRepository.findAll();
    }

    @PostMapping("/createPlan")
    public String createPlan(@RequestBody examRequest examRequest){
        String subjectName = examRequest.getSubject();
        int hoursToStudy = examRequest.getTotalHours();

        studySession studySession = new studySession(
                LocalDate.now().plusDays(1),
                (hoursToStudy * 60) / 2,
                "Theoretical review of " + subjectName + " using Active Recall."
        );

        studySession studySession2 = new studySession(
                examRequest.getExamDate().minusDays(1),
                (hoursToStudy * 60) / 2,
                "Practical mock exam for " + subjectName + " under real time constraints."
        );

        sessionRepository.save(studySession);
        sessionRepository.save(studySession2);

        return "Successfully created and saved a 2-session study plan for " + subjectName + "!";
    }
}
