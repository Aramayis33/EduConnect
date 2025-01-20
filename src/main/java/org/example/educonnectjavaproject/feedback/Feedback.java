package org.example.educonnectjavaproject.feedback;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int studentId;
    private Date date;
    @Column(length = 45, nullable = false)
    private String comment;
    public Feedback() {}

    public Feedback(int id, int studentId, Date date, String comment) {
        this.id = id;
        this.studentId = studentId;
        this.date = date;
        this.comment = comment;
    }

    public Feedback(int studentId, Date date, String comment) {
        this.studentId = studentId;
        this.date = date;
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
