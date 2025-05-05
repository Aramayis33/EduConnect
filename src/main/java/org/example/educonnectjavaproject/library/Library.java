package org.example.educonnectjavaproject.library;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.security.Admin;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.teacher.Teacher;

import javax.security.auth.Subject;

@Entity
@Table(name = "`library`")
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subjects subject;
    private String fileName;
    private int grade;
    private int bookPublicationYear;
    private String bookAuthor;
    private String thumbnailImage;

@ManyToOne
@JoinColumn(name="teacher_id")
private Teacher teacher;

@ManyToOne
@JoinColumn(name="admin_id")
private Admin admin;

private int isConfirmed;

    public Library() {
    }

    public Library(String title, Subjects subject, String fileName, int grade, int bookPublicationYear, String bookAuthor, String thumbnailImage, Teacher teacher, Admin admin, int isConfirmed) {
        this.title = title;
        this.subject = subject;
        this.fileName = fileName;
        this.grade = grade;
        this.bookPublicationYear = bookPublicationYear;
        this.bookAuthor = bookAuthor;
        this.thumbnailImage = thumbnailImage;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getBookPublicationYear() {
        return bookPublicationYear;
    }

    public void setBookPublicationYear(int bookPublicationYear) {
        this.bookPublicationYear = bookPublicationYear;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
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