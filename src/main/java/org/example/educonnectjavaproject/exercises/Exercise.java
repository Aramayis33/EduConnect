package org.example.educonnectjavaproject.exercises;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.filegroup.FileGroup;
import org.example.educonnectjavaproject.homework.HomeWork;
import org.example.educonnectjavaproject.student.Student;

import java.sql.Date;

@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date executionDate;
    @Column(nullable = false)
    private String answerText;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name="file_group_id")
    private FileGroup fileGroup;
    @ManyToOne
    @JoinColumn(name = "homework_id")
    private HomeWork homeWork;
    private int isChecked;

    public Exercise() {
    }

    public Exercise(Date executionDate, String answerText, Student student, FileGroup fileGroup, HomeWork homeWork, int isChecked) {
        this.executionDate = executionDate;
        this.answerText = answerText;
        this.student = student;
        this.fileGroup = fileGroup;
        this.homeWork = homeWork;
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }


    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public HomeWork getHomeWork() {
        return homeWork;
    }

    public void setHomeWork(HomeWork homeWork) {
        this.homeWork = homeWork;
    }

    public FileGroup getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(FileGroup fileGroup) {
        this.fileGroup = fileGroup;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }
}
