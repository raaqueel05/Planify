package com.example.demo;

import java.time.LocalDate;

public class examRequest {
    private String subject;
    private LocalDate examDate;
    private int totalHours;

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public LocalDate getExamDate() {
        return examDate;
    }
    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }
    public int getTotalHours() {
        return totalHours;
    }
    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }
}
