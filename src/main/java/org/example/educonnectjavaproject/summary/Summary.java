package org.example.educonnectjavaproject.summary;

import java.util.List;

public class Summary {

    private double semester1;
    private double semester2;
    private String subject;
    private double afterAll;

    public Summary() {
    }

    public Summary(double semester1, double semester2, String subject, double afterAll) {
        this.semester1 = semester1;
        this.semester2 = semester2;
        this.subject = subject;
        this.afterAll = afterAll;
    }

    public double getSemester1() {
        return semester1;
    }

    public void setSemester1(double semester1) {
        this.semester1 = semester1;
    }

    public double getSemester2() {
        return semester2;
    }

    public void setSemester2(double semester2) {
        this.semester2 = semester2;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public double getAfterAll() {
        return afterAll;
    }

    public void setAfterAll(double afterAll) {
        this.afterAll = afterAll;
    }

}
