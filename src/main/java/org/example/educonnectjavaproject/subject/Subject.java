package org.example.educonnectjavaproject.subject;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.example.educonnectjavaproject.student.Student;

@Entity
@AllArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int math;
    private int english;
    private int java;

    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;

    public Subject() {
    }

    public Subject(int math, int english, int java, Student student) {
        this.math = math;
        this.english = english;
        this.java = java;
        this.student = student;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getMath() {
        return math;
    }

    public void setMath(int math) {
        this.math = math;
    }

    public int getEnglish() {
        return english;
    }

    public void setEnglish(int english) {
        this.english = english;
    }

    public int getJava() {
        return java;
    }

    public void setJava(int java) {
        this.java = java;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
