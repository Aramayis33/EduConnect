package org.example.educonnectjavaproject.ratings;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.subject.Subjects;

import java.sql.Date;

@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int rating;
    private Date date;
    @ManyToOne
    @JoinColumn(name="subject_id")
   private Subjects subject;
    @ManyToOne
    @JoinColumn(name="teacher_id")
   private Teacher teacher;
    @ManyToOne
    @JoinColumn(name = "student_id")
   private Student student;

    private String ratingType;

    @Column(nullable = false)
    private String topic;

    private String comment;

    private int isPresent;
    private int semester;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
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

    public String getRatingType() {
        return ratingType;
    }

    public void setRatingType(String ratingType) {
        this.ratingType = ratingType;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(int isPresent) {
        this.isPresent = isPresent;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public Rating() {
    }

    public Rating(int rating, Date date, Subjects subject, Teacher teacher, Student student, String ratingType, String topic, String comment, int isPresent, int semester) {
        this.rating = rating;
        this.date = date;
        this.subject = subject;
        this.teacher = teacher;
        this.student = student;
        this.ratingType = ratingType;
        this.topic = topic;
        this.comment = comment;
        this.isPresent = isPresent;
        this.semester=semester;
    }
}
