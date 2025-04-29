package org.example.educonnectjavaproject.feedback;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.teacher.Teacher;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name="teacher_id")
    private Teacher teacher;
    private Timestamp date;
    @Column(length = 45, nullable = false)
    private String comment;
    public Feedback() {}



    public Feedback(Student student, Teacher teacher ,Timestamp date, String comment) {
        this.student = student;
        this.teacher = teacher;
        this.date = date;
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
