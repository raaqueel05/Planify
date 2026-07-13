package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name= "studySession")
public class studySession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private int duration;

    @Column(columnDefinition = "TEXT")
    private String studyMethod;

    public studySession() {}

    public studySession(LocalDate date, int duration, String studyMethod) {
        this.date = date;
        this.duration = duration;
        this.studyMethod = studyMethod;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getStudyMethod() {
        return studyMethod;
    }
    public void setStudyMethod(String studyMethod) {
        this.studyMethod = studyMethod;
    }
}
