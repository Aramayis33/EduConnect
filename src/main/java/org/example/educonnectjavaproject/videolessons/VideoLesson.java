package org.example.educonnectjavaproject.videolessons;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.security.Admin;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.teacher.Teacher;

import javax.security.auth.Subject;

@Entity
public class VideoLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="subject_id")
    private Subjects subject;
    private int grade;
    private String title;
    private String videoUrl;
    private String description;

    @ManyToOne
    @JoinColumn(name="teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name="admin_id")
    private Admin admin;

    private int isConfirmed;

    public VideoLesson() {
    }

    public VideoLesson(Subjects subject, int grade, String title, String videoUrl, String description, Teacher teacher, Admin admin, int isConfirmed) {
        this.subject = subject;
        this.grade = grade;
        this.title = title;
        this.videoUrl = videoUrl;
        this.description = description;
        this.teacher = teacher;
        this.admin = admin;
        this.isConfirmed = isConfirmed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public int getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(int isConfirmed) {
        this.isConfirmed = isConfirmed;
    }
}
