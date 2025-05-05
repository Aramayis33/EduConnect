package org.example.educonnectjavaproject.teacher;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.feedback.Feedback;
import org.example.educonnectjavaproject.homework.HomeWork;
import org.example.educonnectjavaproject.security.LogInHistory;
import org.example.educonnectjavaproject.subject.SubjectGroup;
import org.example.educonnectjavaproject.subject.Subjects;

import javax.security.auth.Subject;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Teacher {
    public Teacher() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 45, nullable = false)
    private String name;

    @Column(length = 45, nullable = false)
    private String surname;

    @Column(length = 100, nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectGroup subjectGroup;

    @Column(length = 100, nullable = false)
    private String password;

    private int isActivated;

    private String image;

    private int isBlocked;

    private Timestamp deletedAt;

    public Teacher(String name, String surname, String email, SubjectGroup subjectGroup, String password, int isActivated, String image, int isBlocked, Timestamp deletedAt) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.subjectGroup = subjectGroup;
        this.password = password;
        this.isActivated = isActivated;
        this.image = image;
        this.isBlocked = isBlocked;
        this.deletedAt=deletedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(int isActivated) {
        this.isActivated = isActivated;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public SubjectGroup getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(SubjectGroup subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public int getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(int isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
