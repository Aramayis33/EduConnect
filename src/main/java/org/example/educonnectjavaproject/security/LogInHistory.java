package org.example.educonnectjavaproject.security;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.teacher.Teacher;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
public class LogInHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;

    private String ipAddress;

    private String userAgent;

    private Timestamp loginTime;

    public LogInHistory() {
    }

    public LogInHistory(Teacher teacher, Student student, String ipAddress, String userAgent, Timestamp loginTime) {
        this.teacher = teacher;
        this.student = student;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.loginTime = loginTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

}
